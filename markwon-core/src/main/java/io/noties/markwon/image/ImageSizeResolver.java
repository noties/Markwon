package io.noties.markwon.image;

import android.graphics.Rect;

import androidx.annotation.NonNull;

/**
 * @see ImageSizeResolverDef
 * @see io.noties.markwon.MarkwonConfiguration.Builder#imageSizeResolver(ImageSizeResolver)
 * @since 1.0.1
 */
public abstract class ImageSizeResolver {

    /**
     * @since 4.0.0
     */
    @NonNull
    public abstract Rect resolveImageSize(@NonNull AsyncDrawable drawable);
}
