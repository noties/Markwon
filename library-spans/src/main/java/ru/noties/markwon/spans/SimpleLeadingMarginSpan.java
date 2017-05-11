package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class SimpleLeadingMarginSpan implements LeadingMarginSpan {

    private final int margin;

    public SimpleLeadingMarginSpan(int margin) {
        this.margin = margin;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return margin;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        // no op
    }
}
