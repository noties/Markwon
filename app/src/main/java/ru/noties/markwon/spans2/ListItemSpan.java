package ru.noties.markwon.spans2;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;
import ru.noties.debug.Debug;

public class ListItemSpan implements LeadingMarginSpan {

    private final int blockIndent;
    private final boolean nested;
    private final int start;

    public ListItemSpan(int blockIndent, boolean nested, int start) {
        this.blockIndent = blockIndent;
        this.nested = nested;
        this.start = start;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return 36;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
//        Debug.i("x: %d, dir: %d, top: %d, baseline: %d, bottom: %d, first: %s",
//                x, dir, top, baseline, bottom, first
//        );

        // if there was a line break, we don't need to draw it
        if (this.start != start) {
            return;
        }

        final int save = c.save();
        try {
            final int left = 24 * (blockIndent - 1) + (first ? 12 : 0);
            final RectF rectF = new RectF(left, top, left + 16, bottom);
            final Paint paint = new Paint();
            paint.setStyle(nested ? Paint.Style.STROKE : Paint.Style.FILL);
            paint.setColor(0xFFff0000);
            c.drawOval(rectF, paint);
        } finally {
            c.restoreToCount(save);
        }
    }
}
