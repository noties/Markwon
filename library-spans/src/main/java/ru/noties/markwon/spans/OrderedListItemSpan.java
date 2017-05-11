package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class OrderedListItemSpan implements LeadingMarginSpan {

    public static class Config {

        final int marginWidth; // by default 0
        final int numberColor; // by default color of the main text

        public Config(@IntRange(from = 0) int marginWidth, @ColorInt int numberColor) {
            this.marginWidth = marginWidth;
            this.numberColor = numberColor;
        }
    }

    private final Config config;
    private final String number;
    private final int blockIndent;
    private final int start;

    public OrderedListItemSpan(
            @NonNull Config config,
            @NonNull String number,
            @IntRange(from = 0) int blockIndent,
            @IntRange(from = 0) int start
    ) {
        this.config = config;
        this.number = number;
        this.blockIndent = blockIndent;
        this.start = start;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return config.marginWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        // if there was a line break, we don't need to draw anything
        if (this.start != start) {
            return;
        }

        if (config.numberColor != 0) {
            p.setColor(config.numberColor);
        }

        final int width = config.marginWidth;
        final int numberWidth = (int) (p.measureText(number) + .5F);
        final int numberX = (width * blockIndent) - numberWidth;

        final int numberY = bottom - ((bottom - top) / 2) - (int) ((p.descent() + p.ascent()) / 2);

        c.drawText(number, numberX, numberY, p);
    }
}
