package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class ThematicBreakSpan implements LeadingMarginSpan {

    private final SpannableTheme mTheme;
    private final Rect mRect = ObjectsPool.rect();
    private final Paint mPaint = ObjectsPool.paint();

    public ThematicBreakSpan(@NonNull SpannableTheme theme) {
        mTheme = theme;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        final int middle = top + ((bottom - top) / 2);

        mPaint.set(p);
        mTheme.applyThematicBreakStyle(mPaint);

        final int height = (int) (mPaint.getStrokeWidth() + .5F);
        final int halfHeight = (int) (height / 2.F + .5F);

        mRect.set(x, middle - halfHeight, c.getWidth(), middle + halfHeight);
        c.drawRect(mRect, mPaint);
    }
}
