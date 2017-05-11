package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class BlockQuoteSpan implements LeadingMarginSpan {

    private static final int DEF_COLOR_ALPHA = 50;

    @SuppressWarnings("WeakerAccess")
    public static class Config {

        final int totalWidth;
        final int quoteWidth; // by default 1/4 of width
        final int quoteColor; // by default textColor with 0.2 alpha

        public Config(
                @IntRange(from = 1) int totalWidth,
                @IntRange(from = 0) int quoteWidth,
                @ColorInt int quoteColor) {
            this.totalWidth = totalWidth;
            this.quoteWidth = quoteWidth;
            this.quoteColor = quoteColor;
        }
    }

    private final Config config;
    private final Rect rect = ObjectsPool.rect();
    private final Paint paint = ObjectsPool.paint();
    private final int indent;

    public BlockQuoteSpan(@NonNull Config config, int indent) {
        this.config = config;
        this.indent = indent;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return config.totalWidth;
    }

    @Override
    public void drawLeadingMargin(
            Canvas c,
            Paint p,
            int x,
            int dir,
            int top,
            int baseline,
            int bottom,
            CharSequence text,
            int start,
            int end,
            boolean first,
            Layout layout) {

        final int width;
        if (config.quoteWidth == 0) {
            width = (int) (config.totalWidth / 4.F + .5F);
        } else {
            width = config.quoteWidth;
        }

        final int color;
        if (config.quoteColor != 0) {
            color = config.quoteColor;
        } else {
            color = ColorUtils.applyAlpha(p.getColor(), DEF_COLOR_ALPHA);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);

        final int left = config.totalWidth * (indent - 1);
        rect.set(left, top, left + width, bottom);

        c.drawRect(rect, paint);
    }
}
