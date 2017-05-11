package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class ThematicBreakSpan implements LeadingMarginSpan {

    private static final int DEF_COLOR_ALPHA = 127;

    public static class Config {

        final int color; // by default textColor with 0.5 alpha
        final int height; // by default strokeWidth of paint

        public Config(@ColorInt int color, @IntRange(from = 0) int height) {
            this.color = color;
            this.height = height;
        }
    }

    private final Config config;
    private final Rect rect = ObjectsPool.rect();
    private final Paint paint = ObjectsPool.paint();

    public ThematicBreakSpan(Config config) {
        this.config = config;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        final int middle = top + ((bottom - top) / 2);

        final int color;
        if (config.color == 0) {
            color = ColorUtils.applyAlpha(p.getColor(), DEF_COLOR_ALPHA);
        } else {
            color = config.color;
        }
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        final int height;
        if (config.height == 0) {
            height = (int) (p.getStrokeWidth() + .5F);
        } else {
            height = config.height;
        }
        final int halfHeight = (int) (height / 2.F + .5F);

        rect.set(x, middle - halfHeight, c.getWidth(), middle + halfHeight);
        c.drawRect(rect, paint);
    }
}
