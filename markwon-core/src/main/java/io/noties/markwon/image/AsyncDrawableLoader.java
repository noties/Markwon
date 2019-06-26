package io.noties.markwon.image;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class AsyncDrawableLoader {

    /**
     * @since 3.0.0
     */
    @NonNull
    public static AsyncDrawableLoader noOp() {
        return new AsyncDrawableLoaderNoOp();
    }

    /**
     * @since 4.0.0
     */
    public abstract void load(@NonNull AsyncDrawable drawable);

    /**
     * @since 4.0.0
     */
    public abstract void cancel(@NonNull AsyncDrawable drawable);

    @Nullable
    public abstract Drawable placeholder(@NonNull AsyncDrawable drawable);

}
