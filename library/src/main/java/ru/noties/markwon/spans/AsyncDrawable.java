package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.renderer.html.ImageSize;
import ru.noties.markwon.renderer.html.ImageSizeResolver;
import ru.noties.markwon.spans.configuration.image.ImageConfig;
import ru.noties.markwon.spans.configuration.image.ImageGravity;
import ru.noties.markwon.spans.configuration.image.ImageWidth;

public class AsyncDrawable extends Drawable {

    private final String destination;

    private final Loader loader;

    private final ImageSize imageSize;

    private final ImageSizeResolver imageSizeResolver;

    private Drawable result;

    private Callback callback;

    private int canvasWidth;

    private float textSize;

    public AsyncDrawable(@NonNull String destination, @NonNull Loader loader) {
        this(destination, loader, null, null);
    }

    /**
     * @since 1.0.1
     */
    public AsyncDrawable(
        @NonNull String destination,
        @NonNull Loader loader,
        @Nullable ImageSizeResolver imageSizeResolver,
        @Nullable ImageSize imageSize
    ) {
        this.destination = destination;
        this.loader = loader;
        this.imageSizeResolver = imageSizeResolver;
        this.imageSize = imageSize;
    }

    public String getDestination() {
        return destination;
    }

    public Drawable getResult() {
        return result;
    }

    public boolean hasResult() {
        return result != null;
    }

    public boolean isAttached() {
        return getCallback() != null;
    }

    // yeah
    public void setCallback2(@Nullable Callback callback) {

        this.callback = callback;
        super.setCallback(callback);

        // if not null -> means we are attached
        if (callback != null) {
            loader.load(destination, this);
        } else {
            if (result != null) {

                result.setCallback(null);

                // let's additionally stop if it Animatable
                if (result instanceof Animatable) {
                    ((Animatable) result).stop();
                }
            }
            loader.cancel(destination);
        }
    }

    public void setResult(@NonNull Drawable result, @NonNull ImageConfig imageConfig) {

        // if we have previous one, detach it
        if (this.result != null) {
            this.result.setCallback(null);
        }

        this.result = result;
        this.result.setCallback(callback);

        final Rect bounds = resolveBounds(imageConfig);
        result.setBounds(bounds);
        setBounds(bounds);

        invalidateSelf();
    }

    /**
     * @since 1.0.1
     */
    @SuppressWarnings("WeakerAccess")
    public void initWithKnownDimensions(int width, float textSize) {
        this.canvasWidth = width;
        this.textSize = textSize;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (hasResult()) {
            result.draw(canvas);
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        final int opacity;
        if (hasResult()) {
            opacity = result.getOpacity();
        } else {
            opacity = PixelFormat.TRANSPARENT;
        }
        return opacity;
    }

    @Override
    public int getIntrinsicWidth() {
        final int out;
        if (hasResult()) {
            out = result.getIntrinsicWidth();
        } else {
            out = 0;
        }
        return out;
    }

    @Override
    public int getIntrinsicHeight() {
        final int out;
        if (hasResult()) {
            out = result.getIntrinsicHeight();
        } else {
            out = 0;
        }
        return out;
    }

    /**
     * @param imageConfig
     * @since 1.0.1
     */
    @NonNull
    private Rect resolveBounds(@NonNull ImageConfig imageConfig) {
        final Rect rect;
        if (imageSizeResolver == null
            || imageSize == null) {
            rect = result.getBounds();
        } else {
            rect = imageSizeResolver.resolveImageSize(imageSize, result.getBounds(), canvasWidth, textSize);
        }
        return adjustBounds(rect, imageConfig);
    }

    private Rect adjustBounds(Rect bounds, ImageConfig imageConfig) {
        final ImageGravity gravity = imageConfig.getGravity();
        final ImageWidth imageWidth = imageConfig.getImageWidth();

        if (imageWidth == ImageWidth.MatchParent) {
            final float growthRatio = (float) canvasWidth / bounds.width();
            bounds.left = 0;
            bounds.right = canvasWidth;
            bounds.bottom = (int) (bounds.top + bounds.height() * growthRatio);
        } else {
            switch (gravity) {
                case Left:
                    //left is unchanged
                    break;
                case Right:
                    bounds.left = canvasWidth - bounds.width();
                    bounds.right = canvasWidth;
                    break;
                case Center:
                    final int center = canvasWidth / 2;
                    final int imageRadius = bounds.width() / 2;
                    bounds.left = center - imageRadius;
                    bounds.right = center + imageRadius;
                    break;
            }
        }

        return bounds;
    }

    public interface Loader {

        void load(@NonNull String destination, @NonNull AsyncDrawable drawable);

        void cancel(@NonNull String destination);
    }
}
