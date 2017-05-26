package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class OrderedListItemSpan implements LeadingMarginSpan {

    private final SpannableTheme theme;
    private final String number;
    private final int blockIndent;
    private final int start;

    public OrderedListItemSpan(
            @NonNull SpannableTheme theme,
            @NonNull String number,
            @IntRange(from = 0) int blockIndent,
            @IntRange(from = 0) int start
    ) {
        this.theme = theme;
        this.number = number;
        this.blockIndent = blockIndent;
        this.start = start;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return theme.getBlockMargin();
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        // if there was a line break, we don't need to draw anything
        if (this.start != start) {
            return;
        }

        theme.applyListItemStyle(p);

        final int width = theme.getBlockMargin();
        final int numberWidth = (int) (p.measureText(number) + .5F);
        final int numberX = (width * blockIndent) - numberWidth;

        final float numberY = CanvasUtils.textCenterY(top, bottom, p);

        c.drawText(number, numberX, numberY, p);
    }
}
