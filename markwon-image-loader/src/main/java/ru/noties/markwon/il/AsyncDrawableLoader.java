package ru.noties.markwon.il;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;
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

    private final ExecutorService executorService;
    private final Handler mainThread;
    private final Drawable errorDrawable;
    private final Map<String, SchemeHandler> schemeHandlers;
    private final List<MediaDecoder> mediaDecoders;

    private final Map<String, Future<?>> requests;

    AsyncDrawableLoader(Builder builder) {
        this.executorService = builder.executorService;
        this.mainThread = new Handler(Looper.getMainLooper());
        this.errorDrawable = builder.errorDrawable;
        this.schemeHandlers = builder.schemeHandlers;
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

        for (SchemeHandler schemeHandler : schemeHandlers.values()) {
            schemeHandler.cancel(destination);
        }
    }

    private Future<?> execute(@NonNull final String destination, @NonNull AsyncDrawable drawable) {

        final WeakReference<AsyncDrawable> reference = new WeakReference<AsyncDrawable>(drawable);

        // todo: should we cancel pending request for the same destination?
        //      we _could_ but there is possibility that one resource is request in multiple places

        // todo: error handing (simply applying errorDrawable is not a good solution
        //      as reason for an error is unclear (no scheme handler, no input data, error decoding, etc)

        // todo: more efficient ImageMediaDecoder... BitmapFactory.decodeStream is a bit not optimal
        //      for big images for sure. We _could_ introduce internal Drawable that will check for
        //      image bounds (but we will need to cache inputStream in order to inspect and optimize
        //      input image...)

        // todo, if not a link -> show placeholder

        return executorService.submit(new Runnable() {
            @Override
            public void run() {

                final ImageItem item;

                final Uri uri = Uri.parse(destination);

                final SchemeHandler schemeHandler = schemeHandlers.get(uri.getScheme());
                if (schemeHandler != null) {
                    item = schemeHandler.handle(destination, uri);
                } else {
                    item = null;
                }

                final InputStream inputStream = item != null
                        ? item.inputStream()
                        : null;

                Drawable result = null;

                if (inputStream != null) {
                    try {

                        final String fileName = item.fileName();
                        final MediaDecoder mediaDecoder = fileName != null
                                ? mediaDecoderFromFile(fileName)
                                : mediaDecoderFromContentType(item.contentType());

                        if (mediaDecoder != null) {
                            result = mediaDecoder.decode(inputStream);
                        }

                    } finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            // ignored
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

    // todo: as now we have different layers of abstraction (for scheme handling and media decoding)
    //      we no longer should add dependencies implicitly, it would be way better to allow adding
    //      multiple artifacts (file, data, network, svg, gif)... at least, maybe we can extract API
    //      for this module (without implementations), but keep _all-in_ (fat) artifact with all of these.
    public static class Builder {

        private OkHttpClient client;
        private Resources resources;
        private ExecutorService executorService;
        private Drawable errorDrawable;

        // @since 2.0.0
        private final Map<String, SchemeHandler> schemeHandlers = new HashMap<>(3);

        // @since 1.1.0
        private final List<MediaDecoder> mediaDecoders = new ArrayList<>(3);


        @NonNull
        @Deprecated
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

        /**
         * @since 2.0.0
         */
        @NonNull
        public Builder addSchemeHandler(@NonNull SchemeHandler schemeHandler) {

            SchemeHandler previous;

            for (String scheme : schemeHandler.schemes()) {
                previous = schemeHandlers.put(scheme, schemeHandler);
                if (previous != null) {
                    throw new IllegalStateException(String.format("Multiple scheme handlers handle " +
                            "the same scheme: `%s`, %s %s", scheme, previous, schemeHandler));
                }
            }

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

            // I think we should deprecate this...
            if (resources == null) {
                resources = Resources.getSystem();
            }

            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();
            }

            // @since 2.0.0
            // put default scheme handlers (to mimic previous behavior)
            if (schemeHandlers.size() == 0) {
                if (client == null) {
                    client = new OkHttpClient();
                }
                addSchemeHandler(NetworkSchemeHandler.create(client));
                addSchemeHandler(FileSchemeHandler.createWithAssets(resources.getAssets()));
                addSchemeHandler(DataUriSchemeHandler.create());
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
}
