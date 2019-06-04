package io.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class AsyncDrawableLoaderImpl extends AsyncDrawableLoader {

    private final ExecutorService executorService;
    private final Map<String, SchemeHandler> schemeHandlers;
    private final Map<String, MediaDecoder> mediaDecoders;
    private final MediaDecoder defaultMediaDecoder;
    private final ImagesPlugin.PlaceholderProvider placeholderProvider;
    private final ImagesPlugin.ErrorHandler errorHandler;

    private final Handler handler;

    // @since 4.0.0-SNAPSHOT use a hash-map with a AsyncDrawable as key for multiple requests
    //  for the same destination
    private final Map<AsyncDrawable, Future<?>> requests = new HashMap<>(2);

    AsyncDrawableLoaderImpl(@NonNull AsyncDrawableLoaderBuilder builder) {
        this(builder, new Handler(Looper.getMainLooper()));
    }

    // @since 4.0.0-SNAPSHOT
    @VisibleForTesting
    AsyncDrawableLoaderImpl(@NonNull AsyncDrawableLoaderBuilder builder, @NonNull Handler handler) {
        this.executorService = builder.executorService;
        this.schemeHandlers = builder.schemeHandlers;
        this.mediaDecoders = builder.mediaDecoders;
        this.defaultMediaDecoder = builder.defaultMediaDecoder;
        this.placeholderProvider = builder.placeholderProvider;
        this.errorHandler = builder.errorHandler;
        this.handler = handler;
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
    public Drawable placeholder(@NonNull AsyncDrawable drawable) {
        return placeholderProvider != null
                ? placeholderProvider.providePlaceholder(drawable)
                : null;
    }

    @NonNull
    private Future<?> execute(@NonNull final AsyncDrawable asyncDrawable) {

        // todo: more efficient DefaultImageMediaDecoder... BitmapFactory.decodeStream is a bit not optimal
        //      for big images for sure. We _could_ introduce internal Drawable that will check for
        //      image bounds (but we will need to cache inputStream in order to inspect and optimize
        //      input image...)

        return executorService.submit(new Runnable() {
            @Override
            public void run() {

                final String destination = asyncDrawable.getDestination();

                final Uri uri = Uri.parse(destination);

                Drawable drawable = null;

                try {
                    // obtain scheme handler
                    final SchemeHandler schemeHandler = schemeHandlers.get(uri.getScheme());
                    if (schemeHandler != null) {

                        // handle scheme
                        final ImageItem imageItem = schemeHandler.handle(destination, uri);

                        // if resulting imageItem needs further decoding -> proceed
                        if (imageItem.hasDecodingNeeded()) {

                            final ImageItem.WithDecodingNeeded withDecodingNeeded = imageItem.getAsWithDecodingNeeded();

                            MediaDecoder mediaDecoder = mediaDecoders.get(withDecodingNeeded.contentType());

                            if (mediaDecoder == null) {
                                mediaDecoder = defaultMediaDecoder;
                            }

                            if (mediaDecoder != null) {
                                drawable = mediaDecoder.decode(withDecodingNeeded.contentType(), withDecodingNeeded.inputStream());
                            } else {
                                // throw that no media decoder is found
                                throw new IllegalStateException("No media-decoder is found: " + destination);
                            }
                        } else {
                            drawable = imageItem.getAsWithResult().result();
                        }
                    } else {
                        // throw no scheme handler is available
                        throw new IllegalStateException("No scheme-handler is found: " + destination);
                    }

                } catch (Throwable t) {
                    if (errorHandler != null) {
                        drawable = errorHandler.handleError(destination, t);
                    } else {
                        // else simply log the error
                        Log.e("MARKWON-IMAGE", "Error loading image: " + destination, t);
                    }
                }

                final Drawable out = drawable;

                handler.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        // validate that
                        // * request was not cancelled
                        // * out-result is present
                        // * async-drawable is attached
                        final Future<?> future = requests.remove(asyncDrawable);
                        if (future != null
                                && out != null
                                && asyncDrawable.isAttached()) {
                            asyncDrawable.setResult(out);
                        }
                    }
                }, asyncDrawable, SystemClock.uptimeMillis());
            }
        });
    }
}
