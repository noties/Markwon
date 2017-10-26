package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.MetricAffectingSpan;

public class HeadingSpan extends MetricAffectingSpan implements LeadingMarginSpan {

    private final SpannableTheme mTheme;
    private final Rect mRect = ObjectsPool.rect();
    private final Paint mPaint = ObjectsPool.paint();
    private final int mLevel;
    private final int mTextLength;

    public HeadingSpan(@NonNull SpannableTheme theme, @IntRange(from = 1, to = 6) int level, @IntRange(from = 0) int textLength) {
        mTheme = theme;
        mLevel = level;
        mTextLength = textLength;
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        apply(p);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        apply(tp);
    }

    private void apply(TextPaint paint) {
        mTheme.applyHeadingTextStyle(paint, mLevel);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        // no margin actually, but we need to access Canvas to draw break
        return 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        if (mLevel == 1
                || mLevel == 2) {

            if ((start + mTextLength) == end) {
                mPaint.set(p);

                mTheme.applyHeadingBreakStyle(mPaint);

                final float height = mPaint.getStrokeWidth();
                final int b = (int) (bottom - height + .5F);

                mRect.set(x, b, c.getWidth(), bottom);
                c.drawRect(mRect, mPaint);
            }
        }
    }
}
