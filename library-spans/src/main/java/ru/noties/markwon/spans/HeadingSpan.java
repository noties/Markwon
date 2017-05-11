package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.MetricAffectingSpan;

public class HeadingSpan extends MetricAffectingSpan implements LeadingMarginSpan {

    // taken from html spec (most browsers render headings like that)
    private static final float[] HEADING_SIZES = {
            2.F, 1.5F, 1.17F, 1.F, .83F, .67F,
    };

    public static class Config {

        final int breakHeight; // by default stroke width
        final int breakColor; // by default -> textColor

        public Config(@IntRange(from = 0) int breakHeight, @ColorInt int breakColor) {
            this.breakHeight = breakHeight;
            this.breakColor = breakColor;
        }
    }

    private final Config config;
    private final Rect rect = new Rect();
    private final Paint paint = new Paint();
    private final int level;
    private final int end;

    public HeadingSpan(@NonNull Config config, @IntRange(from = 1, to = 6) int level, @IntRange(from = 0) int end) {
        this.config = config;
        this.level = level - 1;
        this.end = end;

        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        apply(p);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        apply(tp);
    }

    private void apply(TextPaint paint) {
        paint.setTextSize(paint.getTextSize() * HEADING_SIZES[level]);
        paint.setFakeBoldText(true);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        // no margin actually, but we need to access Canvas to draw break
        return 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        // if we are configured to draw underlines, draw them here

        if (level == 0
                || level == 1) {

            if (this.end == end) {

                final int color;
                final int breakHeight;
                if (config.breakColor == 0) {
                    color = p.getColor();
                } else {
                    color = config.breakColor;
                }
                if (config.breakHeight == 0) {
                    breakHeight = (int) (p.getStrokeWidth() + .5F);
                } else {
                    breakHeight = config.breakHeight;
                }
                paint.setColor(color);

                rect.set(x, bottom - breakHeight, c.getWidth(), bottom);
                c.drawRect(rect, paint);
            }
        }
    }
}
