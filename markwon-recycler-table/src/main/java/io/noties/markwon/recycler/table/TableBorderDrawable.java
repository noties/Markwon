package io.noties.markwon.recycler.table;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

class TableBorderDrawable extends Drawable {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    TableBorderDrawable() {
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (paint.getStrokeWidth() > 0) {
            canvas.drawRect(getBounds(), paint);
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

    void update(@Px int borderWidth, @ColorInt int color) {
        paint.setStrokeWidth(borderWidth);
        paint.setColor(color);
        invalidateSelf();
    }
}
