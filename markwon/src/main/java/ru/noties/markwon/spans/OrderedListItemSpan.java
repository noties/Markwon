package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class OrderedListItemSpan implements LeadingMarginSpan {

    private final SpannableTheme theme;
    private final String number;

    // we will use this variable to check if our order number text exceeds block margin,
    // so we will use it instead of block margin
    // @since 1.0.3
    private int margin;

    public OrderedListItemSpan(
            @NonNull SpannableTheme theme,
            @NonNull String number
    ) {
        this.theme = theme;
        this.number = number;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        // @since 1.0.3
        return margin > 0 ? margin : theme.getBlockMargin();
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        // if there was a line break, we don't need to draw anything
        if (!first
                || !LeadingMarginUtils.selfStart(start, text, this)) {
            return;
        }

        theme.applyListItemStyle(p);

        final int numberWidth = (int) (p.measureText(number) + .5F);

        // @since 1.0.3
        int width = theme.getBlockMargin();
        if (numberWidth > width) {
            width = numberWidth;
            margin = numberWidth;
        } else {
            margin = 0;
        }

        final int left;
        if (dir > 0) {
            left = x + (width * dir) - numberWidth;
        } else {
            left = x + (width * dir) + (width - numberWidth);
        }

        // @since 1.1.1 we are using `baseline` argument to position text
        c.drawText(number, left, baseline, p);
    }
}
