package ru.noties.markwon.image;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AsyncDrawable extends Drawable {

    private final String destination;
    private final AsyncDrawableLoader loader;
    private final ImageSize imageSize;
    private final ImageSizeResolver imageSizeResolver;

    private Drawable result;
    private Callback callback;

    private int canvasWidth;
    private float textSize;

    // @since 2.0.1 for use-cases when image is loaded faster than span is drawn and knows canvas width
    private boolean waitingForDimensions;

    /**
     * @since 1.0.1
     */
    public AsyncDrawable(
            @NonNull String destination,
            @NonNull AsyncDrawableLoader loader,
            @Nullable ImageSizeResolver imageSizeResolver,
            @Nullable ImageSize imageSize
    ) {
        this.destination = destination;
        this.loader = loader;
        this.imageSizeResolver = imageSizeResolver;
        this.imageSize = imageSize;

        final Drawable placeholder = loader.placeholder();
        if (placeholder != null) {

            // process placeholder bounds
            if (placeholder.getBounds().isEmpty()) {
                final Rect rect = DrawableUtils.intrinsicBounds(placeholder);
                placeholder.setBounds(rect);
            }

            // apply placeholder immediately if we have one
            setResult(placeholder);
        }
    }

    @NonNull
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

            // as we have a placeholder now, it's important to check it our placeholder
            // has a proper callback at this point. This is not required in most cases,
            // as placeholder should be static, but if it's not -> it can operate as usual
            if (result != null
                    && result.getCallback() == null) {
                result.setCallback(callback);
            }

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

    public void setResult(@NonNull Drawable result) {

        // if we have previous one, detach it
        if (this.result != null) {
            this.result.setCallback(null);
        }

        this.result = result;
        this.result.setCallback(callback);

        initBounds();
    }

    /**
     * Remove result from this drawable (for example, in case of cancellation)
     *
     * @since 3.0.1-SNAPSHOT
     */
    public void clearResult() {

        final Drawable result = this.result;

        if (result != null) {
            result.setCallback(null);
            this.result = null;

            // clear bounds
            setBounds(0, 0, 0, 0);
        }
    }

    private void initBounds() {

        if (canvasWidth == 0) {
            // we still have no bounds - wait for them
            waitingForDimensions = true;
            return;
        }

        waitingForDimensions = false;

        final Rect bounds = resolveBounds();
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

        if (waitingForDimensions) {
            initBounds();
        }
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
     * @since 1.0.1
     */
    @NonNull
    private Rect resolveBounds() {

        // @since 2.0.0 previously we were checking if image is greater than canvas width here
        //          but as imageSizeResolver won't be null anymore, we should transfer this logic
        //          there
        return imageSizeResolver != null
                ? imageSizeResolver.resolveImageSize(imageSize, result.getBounds(), canvasWidth, textSize)
                : result.getBounds();
    }
}
