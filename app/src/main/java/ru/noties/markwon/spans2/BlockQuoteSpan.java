package ru.noties.markwon.spans2;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;
import ru.noties.debug.Debug;

public class BlockQuoteSpan implements LeadingMarginSpan {

    private final int indent;

    public BlockQuoteSpan(int indent) {
        this.indent = indent;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return 24;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
//        Debug.i("x: %d, dir: %d, top: %d, baseline: %d, bottom: %d, first: %s",
//                x, dir, top, baseline, bottom, first
//        );

        final int save = c.save();
        try {
            final int left = 24 * (indent - 1);
//            final RectF rectF = new RectF(0, 0, 16, 16);
            final Rect rect = new Rect(left, top, left + 8, bottom);
            final Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xFFf0f0f0);
            c.drawRect(rect, paint);
//            c.translate(x, .0F);
//            c.drawOval(rectF, paint);
        } finally {
            c.restoreToCount(save);
        }
    }
}
