package io.noties.markwon.image;

import android.graphics.Rect;

import androidx.annotation.NonNull;

/**
 * @since 1.0.1
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ImageSizeResolver {

    /**
     * @since 4.0.0-SNAPSHOT
     */
    @NonNull
    public abstract Rect resolveImageSize(@NonNull AsyncDrawable drawable);
}
