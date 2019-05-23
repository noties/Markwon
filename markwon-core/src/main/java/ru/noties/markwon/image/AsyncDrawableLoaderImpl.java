package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
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
    private final DrawableProvider placeholderDrawableProvider;
    private final DrawableProvider errorDrawableProvider;

    private final Handler mainThread;

    // @since 3.1.0-SNAPSHOT use a hash-map with a weak AsyncDrawable as key for multiple requests
    //  for the same destination
    private final Map<WeakReference<AsyncDrawable>, Future<?>> requests = new HashMap<>(2);

    AsyncDrawableLoaderImpl(@NonNull Builder builder) {
        this.executorService = builder.executorService;
        this.schemeHandlers = builder.schemeHandlers;
        this.mediaDecoders = builder.mediaDecoders;
        this.defaultMediaDecoder = builder.defaultMediaDecoder;
        this.placeholderDrawableProvider = builder.placeholderDrawableProvider;
        this.errorDrawableProvider = builder.errorDrawableProvider;
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

//    @Override
//    public void load(@NonNull String destination, @NonNull AsyncDrawable drawable) {
//
//        // todo: we cannot reliably identify request by the destination, as if
//        //  markdown input has multiple images with the same destination as source
//        //  we will be tracking only one of them (the one appears the last). We should
//        //  move to AsyncDrawable based identification. This method also _maybe_
//        //  should include the ImageSize (comment @since 3.1.0-SNAPSHOT)
//
//        requests.put(destination, execute(destination, drawable));
//    }
//
//    @Override
//    public void cancel(@NonNull String destination) {
//
//        // todo: as we are moving away from a single request for a destination,
//        //  we should re-evaluate this cancellation logic, as if there are multiple images
//        //  in markdown input all of them will be cancelled (won't delivered), even if
//        //  only a single drawable is detached. Cancellation must also take
//        //  the AsyncDrawable argument (comment @since 3.1.0-SNAPSHOT)
//
//        //
//        final Future<?> request = requests.remove(destination);
//        if (request != null) {
//            request.cancel(true);
//        }
//    }

    @Nullable
    @Override
    public Drawable placeholder() {
        return placeholderDrawableProvider != null
                ? placeholderDrawableProvider.provide()
                : null;
    }

    private Future<?> execute(@NonNull final String destination, @NonNull final WeakReference<AsyncDrawable> reference) {

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
                    result = errorDrawableProvider != null
                            ? errorDrawableProvider.provide()
                            : null;
                }

                final Drawable out = result;

                mainThread.post(new Runnable() {
                    @Override
                    public void run() {

                        if (out != null) {

                            // this doesn't work with markdown input with multiple images with the
                            // same source (comment @since 3.1.0-SNAPSHOT)
//                            final boolean canDeliver = requests.remove(destination) != null;
//                            if (canDeliver) {
//                                final AsyncDrawable asyncDrawable = reference.get();
//                                if (asyncDrawable != null && asyncDrawable.isAttached()) {
//                                    asyncDrawable.setResult(out);
//                                }
//                            }

                            // todo: AsyncDrawable cannot change destination, so if it's
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
