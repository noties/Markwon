package ru.noties.markwon.il;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.noties.markwon.spans.AsyncDrawable;

public class AsyncDrawableLoader implements AsyncDrawable.Loader {

    @NonNull
    public static AsyncDrawableLoader create() {
        return builder().build();
    }

    @NonNull
    public static AsyncDrawableLoader.Builder builder() {
        return new Builder();
    }

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final String FILE_ANDROID_ASSETS = "android_asset";

    private final OkHttpClient client;
    private final Resources resources;
    private final ExecutorService executorService;
    private final Handler mainThread;
    private final Drawable errorDrawable;
    private final List<MediaDecoder> mediaDecoders;

    private final Map<String, Future<?>> requests;

    AsyncDrawableLoader(Builder builder) {
        this.client = builder.client;
        this.resources = builder.resources;
        this.executorService = builder.executorService;
        this.mainThread = new Handler(Looper.getMainLooper());
        this.errorDrawable = builder.errorDrawable;
        this.mediaDecoders = builder.mediaDecoders;
        this.requests = new HashMap<>(3);
    }


    @Override
    public void load(@NonNull String destination, @NonNull AsyncDrawable drawable) {
        // if drawable is not a link -> show loading placeholder...
        requests.put(destination, execute(destination, drawable));
    }

    @Override
    public void cancel(@NonNull String destination) {

        final Future<?> request = requests.remove(destination);
        if (request != null) {
            request.cancel(true);
        }

        final List<Call> calls = client.dispatcher().queuedCalls();
        if (calls != null) {
            for (Call call : calls) {
                if (!call.isCanceled()) {
                    if (destination.equals(call.request().tag())) {
                        call.cancel();
                    }
                }
            }
        }
    }

    private Future<?> execute(@NonNull final String destination, @NonNull AsyncDrawable drawable) {
        final WeakReference<AsyncDrawable> reference = new WeakReference<AsyncDrawable>(drawable);
        // todo, if not a link -> show placeholder
        return executorService.submit(new Runnable() {
            @Override
            public void run() {

                final Item item;
                final boolean isFromFile;

                final Uri uri = Uri.parse(destination);
                if ("file".equals(uri.getScheme())) {
                    item = fromFile(uri);
                    isFromFile = true;
                } else {
                    item = fromNetwork(destination);
                    isFromFile = false;
                }

                Drawable result = null;

                if (item != null
                        && item.inputStream != null) {
                    try {

                        final MediaDecoder mediaDecoder = isFromFile
                                ? mediaDecoderFromFile(item.fileName)
                                : mediaDecoderFromContentType(item.contentType);

                        if (mediaDecoder != null) {
                            result = mediaDecoder.decode(item.inputStream);
                        }

                    } finally {
                        try {
                            item.inputStream.close();
                        } catch (IOException e) {
                            // no op
                        }
                    }
                }

                // if result is null, we assume it's an error
                if (result == null) {
                    result = errorDrawable;
                }

                if (result != null) {
                    final Drawable out = result;
                    mainThread.post(new Runnable() {
                        @Override
                        public void run() {
                            final AsyncDrawable asyncDrawable = reference.get();
                            if (asyncDrawable != null && asyncDrawable.isAttached()) {
                                asyncDrawable.setResult(out);
                            }
                        }
                    });
                }

                requests.remove(destination);
            }
        });
    }

    @Nullable
    private Item fromFile(@NonNull Uri uri) {

        final List<String> segments = uri.getPathSegments();
        if (segments == null
                || segments.size() == 0) {
            // pointing to file & having no path segments is no use
            return null;
        }

        final Item out;
        final InputStream inputStream;

        final boolean assets = FILE_ANDROID_ASSETS.equals(segments.get(0));
        final String fileName = uri.getLastPathSegment();

        if (assets) {
            final StringBuilder path = new StringBuilder();
            for (int i = 1, size = segments.size(); i < size; i++) {
                if (i != 1) {
                    path.append('/');
                }
                path.append(segments.get(i));
            }
            // load assets
            InputStream inner = null;
            try {
                inner = resources.getAssets().open(path.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = inner;
        } else {
            InputStream inner = null;
            try {
                inner = new BufferedInputStream(new FileInputStream(new File(uri.getPath())));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            inputStream = inner;
        }

        if (inputStream != null) {
            out = new Item(fileName, null, inputStream);
        } else {
            out = null;
        }

        return out;
    }

    @Nullable
    private Item fromNetwork(@NonNull String destination) {

        Item out = null;

        final Request request = new Request.Builder()
                .url(destination)
                .tag(destination)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            final ResponseBody body = response.body();
            if (body != null) {
                final InputStream inputStream = body.byteStream();
                if (inputStream != null) {
                    final String contentType = response.header(HEADER_CONTENT_TYPE);
                    out = new Item(null, contentType, inputStream);
                }
            }
        }

        return out;
    }

    @Nullable
    private MediaDecoder mediaDecoderFromFile(@NonNull String fileName) {

        MediaDecoder out = null;

        for (MediaDecoder mediaDecoder : mediaDecoders) {
            if (mediaDecoder.canDecodeByFileName(fileName)) {
                out = mediaDecoder;
                break;
            }
        }

        return out;
    }

    @Nullable
    private MediaDecoder mediaDecoderFromContentType(@Nullable String contentType) {

        MediaDecoder out = null;

        for (MediaDecoder mediaDecoder : mediaDecoders) {
            if (mediaDecoder.canDecodeByContentType(contentType)) {
                out = mediaDecoder;
                break;
            }
        }

        return out;
    }

    public static class Builder {

        private OkHttpClient client;
        private Resources resources;
        private ExecutorService executorService;
        private Drawable errorDrawable;

        // @since 1.1.0
        private final List<MediaDecoder> mediaDecoders = new ArrayList<>(3);


        @NonNull
        public Builder client(@NonNull OkHttpClient client) {
            this.client = client;
            return this;
        }

        /**
         * Supplied resources argument will be used to open files from assets directory
         * and to create default {@link MediaDecoder}\'s which require resources instance
         *
         * @return self
         */
        @NonNull
        public Builder resources(@NonNull Resources resources) {
            this.resources = resources;
            return this;
        }

        @NonNull
        public Builder executorService(@NonNull ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        @NonNull
        public Builder errorDrawable(@NonNull Drawable errorDrawable) {
            this.errorDrawable = errorDrawable;
            return this;
        }

        @NonNull
        public Builder mediaDecoders(@NonNull List<MediaDecoder> mediaDecoders) {
            this.mediaDecoders.clear();
            this.mediaDecoders.addAll(mediaDecoders);
            return this;
        }

        @NonNull
        public Builder mediaDecoders(MediaDecoder... mediaDecoders) {
            this.mediaDecoders.clear();
            if (mediaDecoders != null
                    && mediaDecoders.length > 0) {
                Collections.addAll(this.mediaDecoders, mediaDecoders);
            }
            return this;
        }

        @NonNull
        public AsyncDrawableLoader build() {

            if (client == null) {
                client = new OkHttpClient();
            }

            if (resources == null) {
                resources = Resources.getSystem();
            }

            if (executorService == null) {
                // we will use executor from okHttp
                executorService = client.dispatcher().executorService();
            }

            // add default media decoders if not specified
            if (mediaDecoders.size() == 0) {
                mediaDecoders.add(SvgMediaDecoder.create(resources));
                mediaDecoders.add(GifMediaDecoder.create(true));
                mediaDecoders.add(ImageMediaDecoder.create(resources));
            }

            return new AsyncDrawableLoader(this);
        }
    }

    private static class Item {

        final String fileName;
        final String contentType;
        final InputStream inputStream;

        Item(@Nullable String fileName, @Nullable String contentType, @Nullable InputStream inputStream) {
            this.fileName = fileName;
            this.contentType = contentType;
            this.inputStream = inputStream;
        }
    }
}
