package io.noties.markwon.ext.latex;

import android.graphics.Rect;

import androidx.annotation.NonNull;

import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.ImageSizeResolver;

// we must make drawable fit canvas (if specified), but do not keep the ratio whilst scaling up
// @since 4.0.0
class JLatexBlockImageSizeResolver extends ImageSizeResolver {

    private final boolean fitCanvas;

    JLatexBlockImageSizeResolver(boolean fitCanvas) {
        this.fitCanvas = fitCanvas;
    }

    @NonNull
    @Override
    public Rect resolveImageSize(@NonNull AsyncDrawable drawable) {

        final Rect imageBounds = drawable.getResult().getBounds();
        final int canvasWidth = drawable.getLastKnownCanvasWidth();

        if (fitCanvas) {

            // we modify bounds only if `fitCanvas` is true
            final int w = imageBounds.width();

            if (w < canvasWidth) {
                // increase width and center formula (keep height as-is)
                return new Rect(0, 0, canvasWidth, imageBounds.height());
            }

            // @since 4.0.2 we additionally scale down the resulting formula (keeping the ratio)
            // the thing is - JLatexMathDrawable will do it anyway, but it will modify its own
            // bounds (which AsyncDrawable won't catch), thus leading to an empty space after the formula
            if (w > canvasWidth) {
                // here we must scale it down (keeping the ratio)
                final float ratio = (float) w / imageBounds.height();
                final int h = (int) (canvasWidth / ratio + .5F);
                return new Rect(0, 0, canvasWidth, h);
            }
        }

        return imageBounds;
    }
}
