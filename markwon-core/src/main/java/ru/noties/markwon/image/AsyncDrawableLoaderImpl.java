package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class AsyncDrawableLoaderImpl extends AsyncDrawableLoader {

    private final ExecutorService executorService;
    private final Map<String, SchemeHandler> schemeHandlers;
    private final Map<String, MediaDecoder> mediaDecoders;
    private final MediaDecoder defaultMediaDecoder;
    private final Drawable errorDrawable;

    private final Handler mainThread;

    private final Map<String, Future<?>> requests = new HashMap<>(2);

    AsyncDrawableLoaderImpl(@NonNull Builder builder) {
        this.executorService = builder.executorService;
        this.schemeHandlers = builder.schemeHandlers;
        this.mediaDecoders = builder.mediaDecoders;
        this.defaultMediaDecoder = builder.defaultMediaDecoder;
        this.errorDrawable = builder.errorDrawable;
        this.mainThread = new Handler(Looper.getMainLooper());
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

                        MediaDecoder mediaDecoder = mediaDecoders.get(item.contentType());
                        if (mediaDecoder == null) {
                            mediaDecoder = defaultMediaDecoder;
                        }

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
                            final boolean canDeliver = requests.remove(destination) != null;
                            if (canDeliver) {
                                final AsyncDrawable asyncDrawable = reference.get();
                                if (asyncDrawable != null && asyncDrawable.isAttached()) {
                                    asyncDrawable.setResult(out);
                                }
                            }
                        }
                    });
                } else {
                    requests.remove(destination);
                }
            }
        });
    }
}
