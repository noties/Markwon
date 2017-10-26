package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.MetricAffectingSpan;

public class CodeSpan extends MetricAffectingSpan implements LeadingMarginSpan {

    private final SpannableTheme mTheme;
    private final Rect mRect = ObjectsPool.rect();
    private final Paint mPaint = ObjectsPool.paint();

    private final boolean mMultiline;

    public CodeSpan(@NonNull SpannableTheme theme, boolean multiline) {
        mTheme = theme;
        mMultiline = multiline;
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        apply(p);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        apply(ds);
        if (!mMultiline) {
            ds.bgColor = mTheme.getCodeBackgroundColor(ds);
        }
    }

    private void apply(TextPaint p) {
        mTheme.applyCodeTextStyle(p);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mMultiline ? mTheme.getCodeMultilineMargin() : 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        if (mMultiline) {

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mTheme.getCodeBackgroundColor(p));

            mRect.set(x, top, c.getWidth(), bottom);

            c.drawRect(mRect, mPaint);
        }
    }
}
