package io.noties.markwon.image;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @since 1.0.1
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ImageSizeResolverDef extends ImageSizeResolver {

    // we track these two, others are considered to be pixels
    protected static final String UNIT_PERCENT = "%";
    protected static final String UNIT_EM = "em";

    @NonNull
    @Override
    public Rect resolveImageSize(@NonNull AsyncDrawable drawable) {
        return resolveImageSize(
                drawable.getImageSize(),
                drawable.getResult().getBounds(),
                drawable.getLastKnownCanvasWidth(),
                drawable.getLastKnowTextSize());
    }

    @NonNull
    protected Rect resolveImageSize(
            @Nullable ImageSize imageSize,
            @NonNull Rect imageBounds,
            int canvasWidth,
            float textSize
    ) {

        if (imageSize == null) {
            // @since 2.0.0 post process bounds to fit canvasWidth (previously was inside AsyncDrawable)
            //      must be applied only if imageSize is null
            final Rect rect;
            final int w = imageBounds.width();
            if (w > canvasWidth) {
                final float reduceRatio = (float) w / canvasWidth;
                rect = new Rect(
                        0,
                        0,
                        canvasWidth,
                        (int) (imageBounds.height() / reduceRatio + .5F)
                );
            } else {
                rect = imageBounds;
            }
            return rect;
        }

        final Rect rect;

        final ImageSize.Dimension width = imageSize.width;
        final ImageSize.Dimension height = imageSize.height;

        final int imageWidth = imageBounds.width();
        final int imageHeight = imageBounds.height();

        final float ratio = (float) imageWidth / imageHeight;

        if (width != null) {

            final int w;
            final int h;

            if (UNIT_PERCENT.equals(width.unit)) {
                w = (int) (canvasWidth * (width.value / 100.F) + .5F);
            } else {
                w = resolveAbsolute(width, imageWidth, textSize);
            }

            if (height == null
                    || UNIT_PERCENT.equals(height.unit)) {
                h = (int) (w / ratio + .5F);
            } else {
                h = resolveAbsolute(height, imageHeight, textSize);
            }

            rect = new Rect(0, 0, w, h);

        } else if (height != null) {

            if (!UNIT_PERCENT.equals(height.unit)) {
                final int h = resolveAbsolute(height, imageHeight, textSize);
                final int w = (int) (h * ratio + .5F);
                rect = new Rect(0, 0, w, h);
            } else {
                rect = imageBounds;
            }
        } else {
            rect = imageBounds;
        }

        return rect;
    }

    protected int resolveAbsolute(@NonNull ImageSize.Dimension dimension, int original, float textSize) {
        final int out;
        if (UNIT_EM.equals(dimension.unit)) {
            out = (int) (dimension.value * textSize + .5F);
        } else {
            out = (int) (dimension.value + .5F);
        }
        return out;
    }
}
