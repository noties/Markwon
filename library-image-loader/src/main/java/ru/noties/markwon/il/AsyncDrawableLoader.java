package ru.noties.markwon.il;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
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
import pl.droidsonroids.gif.GifDrawable;
import ru.noties.debug.Debug;
import ru.noties.markwon.spans.AsyncDrawable;

public class AsyncDrawableLoader implements AsyncDrawable.Loader {

    public static AsyncDrawableLoader create() {
        return builder().build();
    }

    public static AsyncDrawableLoader.Builder builder() {
        return new Builder();
    }

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_SVG = "image/svg+xml";
    private static final String CONTENT_TYPE_GIF = "image/gif";

    private final OkHttpClient client;
    private final Resources resources;
    private final ExecutorService executorService;
    private final Handler mainThread;
    private final Drawable errorDrawable;

    private final Map<String, Future<?>> requests;

    AsyncDrawableLoader(Builder builder) {
        this.client = builder.client;
        this.resources = builder.resources;
        this.executorService = builder.executorService;
        this.mainThread = new Handler(Looper.getMainLooper());
        this.errorDrawable = builder.errorDrawable;
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

                Debug.i(destination, response);

                Drawable result = null;

                if (response != null) {

                    final ResponseBody body = response.body();
                    if (body != null) {
                        final InputStream inputStream = body.byteStream();
                        if (inputStream != null) {
                            final String contentType = response.header(HEADER_CONTENT_TYPE);
                            try {
                                // svg can have `image/svg+xml;charset=...`
                                if (CONTENT_TYPE_SVG.equals(contentType)
                                        || (!TextUtils.isEmpty(contentType) && contentType.startsWith(CONTENT_TYPE_SVG))) {
                                    // handle SVG
                                    result = handleSvg(inputStream);
                                } else if (CONTENT_TYPE_GIF.equals(contentType)) {
                                    // handle gif
                                    result = handleGif(inputStream);
                                } else {
                                    result = handleSimple(inputStream);
                                    // just try to decode whatever it is
                                }
                            } finally {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    // no op
                                }
                            }
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

    private Drawable handleSvg(InputStream stream) {

        final Drawable out;

        SVG svg = null;
        try {
            svg = SVG.getFromInputStream(stream);
        } catch (SVGParseException e) {
            e.printStackTrace();
        }

        if (svg == null) {
            out = null;
        } else {

            final float w = svg.getDocumentWidth();
            final float h = svg.getDocumentHeight();
            final float density = resources.getDisplayMetrics().density;

            final int width = (int) (w * density + .5F);
            final int height = (int) (h * density + .5F);

            final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            final Canvas canvas = new Canvas(bitmap);
            canvas.scale(density, density);
            svg.renderToCanvas(canvas);

            out = new BitmapDrawable(resources, bitmap);
            DrawableUtils.intrinsicBounds(out);
        }

        return out;
    }

    private Drawable handleGif(InputStream stream) {

        Drawable out = null;

        final byte[] bytes = readBytes(stream);
        if (bytes != null) {
            try {
                out = new GifDrawable(bytes);
                DrawableUtils.intrinsicBounds(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return out;
    }

    private Drawable handleSimple(InputStream stream) {

        final Drawable out;

        final Bitmap bitmap = BitmapFactory.decodeStream(stream);
        if (bitmap != null) {
            out = new BitmapDrawable(resources, bitmap);
            DrawableUtils.intrinsicBounds(out);
        } else {
            out = null;
        }

        return out;
    }

    private static byte[] readBytes(InputStream stream) {

        byte[] out = null;

        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int length = 1024 * 8;
            final byte[] buffer = new byte[length];
            int read;
            while ((read = stream.read(buffer, 0, length)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            out = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    public static class Builder {

        private OkHttpClient client;
        private Resources resources;
        private ExecutorService executorService;
        private Drawable errorDrawable;

        public Builder client(@NonNull OkHttpClient client) {
            this.client = client;
            return this;
        }

        public Builder resources(@NonNull Resources resources) {
            this.resources = resources;
            return this;
        }

        public Builder executorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Builder errorDrawable(Drawable errorDrawable) {
            this.errorDrawable = errorDrawable;
            return this;
        }

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
            return new AsyncDrawableLoader(this);
        }
    }
}
