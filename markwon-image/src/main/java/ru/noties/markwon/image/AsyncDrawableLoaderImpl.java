package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
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

    private final Handler mainThread;

    // @since 3.1.0-SNAPSHOT use a hash-map with a weak AsyncDrawable as key for multiple requests
    //  for the same destination
    private final Map<WeakReference<AsyncDrawable>, Future<?>> requests = new HashMap<>(2);

    AsyncDrawableLoaderImpl(@NonNull AsyncDrawableLoaderBuilder builder) {
        this.executorService = builder.executorService;
        this.schemeHandlers = builder.schemeHandlers;
        this.mediaDecoders = builder.mediaDecoders;
        this.defaultMediaDecoder = builder.defaultMediaDecoder;
        this.placeholderProvider = builder.placeholderProvider;
        this.errorHandler = builder.errorHandler;
        this.mainThread = new Handler(Looper.getMainLooper());
    }

    @Override
    public void load(@NonNull final AsyncDrawable drawable) {

        // primitive synchronization via main-thread
        if (!isMainThread()) {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    load(drawable);
                }
            });
            return;
        }

        // okay, if by some chance requested drawable already has a future associated -> no-op
        // as AsyncDrawable cannot change `destination` (immutable field)
        // @since 3.1.0-SNAPSHOT
        if (hasTaskAssociated(drawable)) {
            return;
        }

        final WeakReference<AsyncDrawable> reference = new WeakReference<>(drawable);
        requests.put(reference, execute(drawable.getDestination(), reference));
    }

    @Override
    public void cancel(@NonNull final AsyncDrawable drawable) {

        if (!isMainThread()) {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    cancel(drawable);
                }
            });
            return;
        }

        final Iterator<Map.Entry<WeakReference<AsyncDrawable>, Future<?>>> iterator =
                requests.entrySet().iterator();

        AsyncDrawable key;
        Map.Entry<WeakReference<AsyncDrawable>, Future<?>> entry;

        while (iterator.hasNext()) {

            entry = iterator.next();
            key = entry.getKey().get();

            // if key is null or it contains requested AsyncDrawable -> cancel
            if (shouldCleanUp(key) || key == drawable) {
                entry.getValue().cancel(true);
                iterator.remove();
            }
        }
    }

    private boolean hasTaskAssociated(@NonNull AsyncDrawable drawable) {

        final Iterator<Map.Entry<WeakReference<AsyncDrawable>, Future<?>>> iterator =
                requests.entrySet().iterator();

        boolean result = false;

        AsyncDrawable key;
        Map.Entry<WeakReference<AsyncDrawable>, Future<?>> entry;

        while (iterator.hasNext()) {

            entry = iterator.next();
            key = entry.getKey().get();

            // clean-up
            if (shouldCleanUp(key)) {
                entry.getValue().cancel(true);
                iterator.remove();
            } else if (key == drawable) {
                result = true;
                // do not break, let iteration continue to possibly clean-up the rest references
            }
        }

        return result;
    }

    private void cleanUp() {

        final Iterator<Map.Entry<WeakReference<AsyncDrawable>, Future<?>>> iterator =
                requests.entrySet().iterator();

        AsyncDrawable key;
        Map.Entry<WeakReference<AsyncDrawable>, Future<?>> entry;

        while (iterator.hasNext()) {

            entry = iterator.next();
            key = entry.getKey().get();

            // clean-up of already referenced or detached drawables
            if (shouldCleanUp(key)) {
                entry.getValue().cancel(true);
                iterator.remove();
            }
        }
    }

    @Nullable
    @Override
    public Drawable placeholder(@NonNull AsyncDrawable drawable) {
        return placeholderProvider != null
                ? placeholderProvider.providePlaceholder(drawable)
                : null;
    }

    @NonNull
    private Future<?> execute(@NonNull final String destination, @NonNull final WeakReference<AsyncDrawable> reference) {

        // todo: more efficient DefaultImageMediaDecoder... BitmapFactory.decodeStream is a bit not optimal
        //      for big images for sure. We _could_ introduce internal Drawable that will check for
        //      image bounds (but we will need to cache inputStream in order to inspect and optimize
        //      input image...)

        return executorService.submit(new Runnable() {
            @Override
            public void run() {

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

                mainThread.post(new Runnable() {
                    @Override
                    public void run() {

                        if (out != null) {
                            // AsyncDrawable cannot change destination, so if it's
                            //  attached and not garbage-collected, we can deliver the result.
                            //  Note that there is no cache, so attach/detach of drawables
                            //  will always request a new entry.. (comment @since 3.1.0-SNAPSHOT)
                            final AsyncDrawable asyncDrawable = reference.get();
                            if (asyncDrawable != null && asyncDrawable.isAttached()) {
                                asyncDrawable.setResult(out);
                            }
                        }

                        requests.remove(reference);
                        cleanUp();
                    }
                });
            }
        });
    }

    private static boolean shouldCleanUp(@Nullable AsyncDrawable drawable) {
        return drawable == null || !drawable.isAttached();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
