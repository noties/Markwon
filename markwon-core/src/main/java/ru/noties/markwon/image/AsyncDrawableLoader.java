package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    @Nullable
    public abstract Drawable placeholder(@NonNull AsyncDrawable drawable);

}
