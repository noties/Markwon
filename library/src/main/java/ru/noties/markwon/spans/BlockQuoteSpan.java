package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class BlockQuoteSpan implements LeadingMarginSpan {

    private final SpannableTheme mTheme;
    private final Rect mRect = ObjectsPool.rect();
    private final Paint mPaint = ObjectsPool.paint();
    private final int mIndent;

    public BlockQuoteSpan(@NonNull SpannableTheme theme, int indent) {
        mTheme = theme;
        mIndent = indent;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mTheme.getBlockMargin();
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

        final int width = mTheme.getBlockQuoteWidth();

        mTheme.applyBlockQuoteStyle(mPaint);

        final int left = mTheme.getBlockMargin() * (mIndent - 1);
        mRect.set(left, top, left + width, bottom);

        c.drawRect(mRect, mPaint);
    }
}
