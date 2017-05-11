package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class BulletListItemSpan implements LeadingMarginSpan {

    // todo, there are 3 types of bullets: filled circle, stroke circle & filled rectangle
    // also, there are ordered lists

    public static class Config {

        final int bulletColor; // by default uses text color
        final int marginWidth;
        final int bulletStrokeWidth;

        // from 0 but it makes sense to provide something wider
        public Config(@ColorInt int bulletColor, @IntRange(from = 0) int marginWidth, int bulletStrokeWidth) {
            this.bulletColor = bulletColor;
            this.marginWidth = marginWidth;
            this.bulletStrokeWidth = bulletStrokeWidth;
        }
    }

    private final Config config;

    private final Paint paint = ObjectsPool.paint();
    private final RectF circle = ObjectsPool.rectF();
    private final Rect rectangle = ObjectsPool.rect();

    private final int blockIndent;
    private final int level;
    private final int start;

    public BulletListItemSpan(
            @NonNull Config config,
            @IntRange(from = 0) int blockIndent,
            @IntRange(from = 0) int level,
            @IntRange(from = 0) int start) {
        this.config = config;
        this.blockIndent = blockIndent;
        this.level = level;
        this.start = start;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return config.marginWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        // if there was a line break, we don't need to draw it
        if (this.start != start) {
            return;
        }

        final int color;
        final float stroke;

        if (config.bulletColor == 0) {
            color = p.getColor();
        } else {
            color = config.bulletColor;
        }

        if (config.bulletStrokeWidth == 0) {
            stroke = p.getStrokeWidth();
        } else {
            stroke = config.bulletStrokeWidth;
        }

        paint.setColor(color);
        paint.setStrokeWidth(stroke);

        final int save = c.save();
        try {

            // by default we use half of margin width, but if height is less than width, we calculate from it
            final int width = config.marginWidth;
            final int height = bottom - top;

            final int side = Math.min(config.marginWidth, height) / 2;

            final int marginLeft = (width - side) / 2;
            final int marginTop = (height - side) / 2;

            final int l = (width * (blockIndent - 1)) + marginLeft;
            final int t = top + marginTop;
            final int r = l + side;
            final int b = t + side;

            if (level == 0
                    || level == 1) {

                circle.set(l, t, r, b);

                final Paint.Style style = level == 0
                        ? Paint.Style.FILL
                        : Paint.Style.STROKE;
                paint.setStyle(style);

                c.drawOval(circle, paint);
            } else {

                rectangle.set(l, t, r, b);

                paint.setStyle(Paint.Style.FILL);

                c.drawRect(rectangle, paint);
            }

        } finally {
            c.restoreToCount(save);
        }
    }
}
