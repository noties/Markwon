package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class BulletListItemSpan implements LeadingMarginSpan {

    private SpannableTheme mTheme;

    private final Paint mPaint = ObjectsPool.paint();
    private final RectF mCircle = ObjectsPool.rectF();
    private final Rect mRectangle = ObjectsPool.rect();

    private final int mBlockIndent;
    private final int mLevel;

    public BulletListItemSpan(
            @NonNull SpannableTheme theme,
            @IntRange(from = 0) int blockIndent,
            @IntRange(from = 0) int level) {
        mTheme = theme;
        mBlockIndent = blockIndent;
        mLevel = level;
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

        mPaint.set(p);

        mTheme.applyListItemStyle(mPaint);

        final int save = c.save();
        try {

            final int width = mTheme.getBlockMargin();
            final int height = bottom - top;

            final int side = mTheme.getBulletWidth(bottom - top);

            final int marginLeft = (width - side) / 2;
            final int marginTop = (height - side) / 2;

            final int l = (width * (mBlockIndent - 1)) + marginLeft;
            final int t = top + marginTop;
            final int r = l + side;
            final int b = t + side;

            if (mLevel == 0
                    || mLevel == 1) {

                mCircle.set(l, t, r, b);

                final Paint.Style style = mLevel == 0
                        ? Paint.Style.FILL
                        : Paint.Style.STROKE;
                mPaint.setStyle(style);

                c.drawOval(mCircle, mPaint);
            } else {

                mRectangle.set(l, t, r, b);

                mPaint.setStyle(Paint.Style.FILL);

                c.drawRect(mRectangle, mPaint);
            }

        } finally {
            c.restoreToCount(save);
        }
    }
}
