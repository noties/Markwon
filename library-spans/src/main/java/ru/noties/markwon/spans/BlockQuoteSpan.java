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

    @SuppressWarnings("WeakerAccess")
    public static class Config {

        final int totalWidth;
        final int quoteWidth;
        final int quoteColor; // by default textColor with 0.1 alpha

        public Config(
                @IntRange(from = 0) int totalWidth,
                @IntRange(from = 0) int quoteWidth,
                @ColorInt int quoteColor) {
            this.totalWidth = totalWidth;
            this.quoteWidth = quoteWidth;
            this.quoteColor = quoteColor;
        }
    }

    private final Config config;
    private final Rect rect = new Rect();
    private final Paint paint = new Paint();
    private final int indent;

    public BlockQuoteSpan(@NonNull Config config, int indent) {
        this.config = config;
        this.indent = indent;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(config.quoteColor);
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

        final int save = c.save();
        try {
            final int left = config.totalWidth * (indent - 1);
            rect.set(left, top, left + config.quoteWidth, bottom);
            c.drawRect(rect, paint);
        } finally {
            c.restoreToCount(save);
        }
    }
}
