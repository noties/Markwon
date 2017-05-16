package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AsyncDrawable extends Drawable {

    public interface Loader {
        void load(@NonNull String destination, @NonNull AsyncDrawable drawable);

        void cancel(@NonNull String destination);
    }

    private final String destination;
    private final Loader loader;

    private Drawable result;
    private Callback callback;

    public AsyncDrawable(@NonNull String destination, @NonNull Loader loader) {
        this.destination = destination;
        this.loader = loader;
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

        // should we copy the data here? like bounds etc?
        // if we are async and we load some image from some source
        // thr bounds might change... so we are better off copy `result` bounds to this instance
        setBounds(result.getBounds());
        invalidateSelf();
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
}
