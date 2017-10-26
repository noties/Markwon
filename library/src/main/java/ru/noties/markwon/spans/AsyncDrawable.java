package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AsyncDrawable extends Drawable {

    public interface Loader {
        void load(@NonNull String destination, @NonNull AsyncDrawable drawable);

        void cancel(@NonNull String destination);
    }

    private final String mDestination;
    private final Loader mLoader;

    private Drawable mResult;
    private Callback mCallback;

    public AsyncDrawable(@NonNull String destination, @NonNull Loader loader) {
        mDestination = destination;
        mLoader = loader;
    }

    public String getDestination() {
        return mDestination;
    }

    public Drawable getResult() {
        return mResult;
    }

    public boolean hasResult() {
        return mResult != null;
    }

    public boolean isAttached() {
        return getCallback() != null;
    }

    // yeah
    public void setCallback2(@Nullable Callback callback) {

        mCallback = callback;
        super.setCallback(callback);

        // if not null -> means we are attached
        if (callback != null) {
            mLoader.load(mDestination, this);
        } else {
            if (mResult != null) {

                mResult.setCallback(null);

                // let's additionally stop if it Animatable
                if (mResult instanceof Animatable) {
                    ((Animatable) mResult).stop();
                }
            }
            mLoader.cancel(mDestination);
        }
    }

    public void setResult(@NonNull Drawable result) {

        // if we have previous one, detach it
        if (mResult != null) {
            mResult.setCallback(null);
        }

        mResult = result;
        mResult.setCallback(mCallback);

        // should we copy the data here? like bounds etc?
        // if we are async and we load some image from some source
        // thr bounds might change... so we are better off copy `mResult` bounds to this instance
        setBounds(result.getBounds());
        invalidateSelf();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (hasResult()) {
            mResult.draw(canvas);
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
            opacity = mResult.getOpacity();
        } else {
            opacity = PixelFormat.TRANSPARENT;
        }
        return opacity;
    }

    @Override
    public int getIntrinsicWidth() {
        final int out;
        if (hasResult()) {
            out = mResult.getIntrinsicWidth();
        } else {
            out = 0;
        }
        return out;
    }

    @Override
    public int getIntrinsicHeight() {
        final int out;
        if (hasResult()) {
            out = mResult.getIntrinsicHeight();
        } else {
            out = 0;
        }
        return out;
    }
}
