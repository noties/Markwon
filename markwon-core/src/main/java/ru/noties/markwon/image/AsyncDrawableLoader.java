package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncDrawableLoader {

    /**
     * @since 3.0.0
     */
    @NonNull
    public static AsyncDrawableLoader noOp() {
        return new AsyncDrawableLoaderNoOp();
    }

    /**
     * @since 3.1.0-SNAPSHOT
     */
    public abstract void load(@NonNull AsyncDrawable drawable);

    /**
     * @since 3.1.0-SNAPSHOT
     */
    public abstract void cancel(@NonNull AsyncDrawable drawable);

    /**
     * @see #load(AsyncDrawable)
     * @deprecated 3.1.0-SNAPSHOT
     */
    @Deprecated
    public void load(@NonNull String destination, @NonNull AsyncDrawable drawable) {
        load(drawable);
    }

    /**
     * Method is deprecated because cancellation won\'t work for markdown input
     * with multiple images with the same source
     *
     * @deprecated 3.1.0-SNAPSHOT
     */
    @Deprecated
    public void cancel(@NonNull String destination) {
        Log.e("MARKWON-IL", "Image loading cancellation must be triggered " +
                "by AsyncDrawable, please use #cancel(AsyncDrawable) method instead. " +
                "No op, nothing is cancelled for destination: " + destination);
    }

    @Nullable
    public abstract Drawable placeholder();
}
