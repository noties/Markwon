package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class OrderedListItemSpan implements LeadingMarginSpan {

    private final SpannableTheme mTheme;
    private final String mNumber;
    private final int mBlockIndent;

    public OrderedListItemSpan(
            @NonNull SpannableTheme theme,
            @NonNull String number,
            @IntRange(from = 0) int blockIndent
    ) {
        mTheme = theme;
        mNumber = number;
        mBlockIndent = blockIndent;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mTheme.getBlockMargin();
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        // if there was a line break, we don't need to draw anything
        if (!first) {
            return;
        }

        mTheme.applyListItemStyle(p);

        final int width = mTheme.getBlockMargin();
        final int numberWidth = (int) (p.measureText(mNumber) + .5F);
        final int numberX = (width * mBlockIndent) - numberWidth;

        final float numberY = CanvasUtils.textCenterY(top, bottom, p);

        c.drawText(mNumber, numberX, numberY, p);
    }
}
