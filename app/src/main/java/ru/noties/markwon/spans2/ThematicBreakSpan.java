package ru.noties.markwon.spans2;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class ThematicBreakSpan implements LeadingMarginSpan {

    @Override
    public int getLeadingMargin(boolean first) {
        return 1;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        final int middle = (bottom - top) / 2;
        final Rect rect = new Rect(0, top + middle - 2, c.getWidth(), top + middle + 2);
        final Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x80000000);
        c.drawRect(rect, paint);
    }
}
