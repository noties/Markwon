package io.noties.markwon.ext.latex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.AsyncDrawableLoader;
import io.noties.markwon.image.ImageSize;
import io.noties.markwon.image.ImageSizeResolver;

/**
 * @since 4.3.0
 */
class JLatextAsyncDrawable extends AsyncDrawable {

    private final boolean isBlock;

    JLatextAsyncDrawable(
            @NonNull String destination,
            @NonNull AsyncDrawableLoader loader,
            @NonNull ImageSizeResolver imageSizeResolver,
            @Nullable ImageSize imageSize,
            boolean isBlock
    ) {
        super(destination, loader, imageSizeResolver, imageSize);
        this.isBlock = isBlock;
    }

    public boolean isBlock() {
        return isBlock;
    }
}
