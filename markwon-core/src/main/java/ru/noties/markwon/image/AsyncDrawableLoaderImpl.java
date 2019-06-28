package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class AsyncDrawableLoaderImpl extends AsyncDrawableLoader {

    private final ExecutorService executorService;
    private final Map<String, SchemeHandler> schemeHandlers;
    private final Map<String, MediaDecoder> mediaDecoders;
    private final MediaDecoder defaultMediaDecoder;
    private final DrawableProvider placeholderDrawableProvider;
    private final DrawableProvider errorDrawableProvider;
    private final Handler handler = new Handler(Looper.getMainLooper());


    // @since 3.1.0-SNAPSHOT use a hash-map with an AsyncDrawable (allows multiple drawables
    // referencing the same source)
    private final Map<AsyncDrawable, Future<?>> requests = new HashMap<>(2);

    AsyncDrawableLoaderImpl(@NonNull Builder builder) {
        this.executorService = builder.executorService;
        this.schemeHandlers = builder.schemeHandlers;
        this.mediaDecoders = builder.mediaDecoders;
        this.defaultMediaDecoder = builder.defaultMediaDecoder;
        this.placeholderDrawableProvider = builder.placeholderDrawableProvider;
        this.errorDrawableProvider = builder.errorDrawableProvider;
    }

    @Override
    public void load(@NonNull final AsyncDrawable drawable) {
        final Future<?> future = requests.get(drawable);
        if (future == null) {
            requests.put(drawable, execute(drawable));
        }
    }

    @Override
    public void cancel(@NonNull final AsyncDrawable drawable) {

        final Future<?> future = requests.remove(drawable);
        if (future != null) {
            future.cancel(true);
        }

        handler.removeCallbacksAndMessages(drawable);
    }

    @Nullable
    @Override
    public Drawable placeholder() {
        return placeholderDrawableProvider != null
                ? placeholderDrawableProvider.provide()
                : null;
    }

    @NonNull
    private Future<?> execute(@NonNull final AsyncDrawable drawable) {

        final String destination = drawable.getDestination();

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
                    result = errorDrawableProvider != null
                            ? errorDrawableProvider.provide()
                            : null;
                }

                final Drawable out = result;

                handler.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        if (requests.remove(drawable) != null) {
                            if (out != null && drawable.isAttached()) {
                                drawable.setResult(out);
                            }
                        }
                    }
                }, drawable, SystemClock.uptimeMillis());
            }
        });
    }
}
