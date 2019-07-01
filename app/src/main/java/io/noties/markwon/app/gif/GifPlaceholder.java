package io.noties.markwon.app.gif;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GifPlaceholder extends Drawable {

    private final Drawable icon;
    private final Paint paint;

    private float left;
    private float top;

    public GifPlaceholder(@NonNull Drawable icon, @ColorInt int background) {
        this.icon = icon;
        if (icon.getBounds().isEmpty()) {
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        }

        if (background != 0) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(background);
        } else {
            paint = null;
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        final int w = bounds.width();
        final int h = bounds.height();

        this.left = (w - icon.getBounds().width()) / 2;
        this.top = (h - icon.getBounds().height()) / 2;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        if (paint != null) {
            canvas.drawRect(getBounds(), paint);
        }

        final int save = canvas.save();
        try {
            canvas.translate(left, top);
            icon.draw(canvas);
        } finally {
            canvas.restoreToCount(save);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        // no op
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        // no op
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
