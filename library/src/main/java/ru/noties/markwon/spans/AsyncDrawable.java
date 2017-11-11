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

public class AsyncDrawable extends Drawable {

    public interface Loader {

        void load(@NonNull String destination, @NonNull AsyncDrawable drawable);

        void cancel(@NonNull String destination);
    }

    private final String destination;
    private final Loader loader;
    private final ImageSize imageSize;

    private Drawable result;
    private Callback callback;

    private int canvasWidth;

    public AsyncDrawable(@NonNull String destination, @NonNull Loader loader) {
        this(destination, loader, null);
    }

    /**
     * @since 1.0.1
     */
    public AsyncDrawable(@NonNull String destination, @NonNull Loader loader, @Nullable ImageSize imageSize) {
        this.destination = destination;
        this.loader = loader;
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

    public void setResult(@NonNull Drawable result) {

        // if we have previous one, detach it
        if (this.result != null) {
            this.result.setCallback(null);
        }

        this.result = result;
        this.result.setCallback(callback);

        final Rect bounds = resolveBounds();
        result.setBounds(bounds);
        setBounds(bounds);

        invalidateSelf();
    }

    /**
     * @since 1.0.1
     */
    @SuppressWarnings("WeakerAccess")
    public void initWithCanvasWidth(int width) {
        this.canvasWidth = width;
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

        return result.getBounds();

//        final Rect rect;
//
//        if (canvasWidth == 0
//                || imageSize == null) {
//
//            rect = result.getBounds();
//
//        } else {
//
//            final Rect bounds = result.getBounds();
//            final float ratio = (float) bounds.width() / bounds.height();
//
//            if (imageSize.widthIsRelative()) {
//
//                final int w = (int) (canvasWidth * ((float) imageSize.width() / 100.F) + .5F);
//                final int h;
//
//                // we still should allow absolute height
//                if (imageSize.height() > 0) {
//                    h = imageSize.height();
//                } else {
//                    h = (int) (w / ratio);
//                }
//
//                rect = new Rect(0, 0, w, h);
//
//            } else {
//
//                // if width is specified, but height not -> calculate by ratio (and vice versa)
//                // else
//
//                final int w;
//                final int h;
//
//                final int width = imageSize.width();
//                final int height = imageSize.height();
//
//                if (width > 0
//                        && height > 0) {
//                    w = width;
//                    h = height;
//                } else if (width > 0) {
//                    w = width;
//                    h = (int) (w / ratio + .5F);
//                } else if (height > 0) {
//                    h = height;
//                    w = (int) (h * ratio + .5F);
//                } else {
//                    w = 0;
//                    h = 0;
//                }
//
//                if (w == 0
//                        || h == 0) {
//                    rect = bounds;
//                } else {
//                    rect = new Rect(0, 0, w, h);
//                }
//            }
//        }
//
//        return rect;
    }
}
