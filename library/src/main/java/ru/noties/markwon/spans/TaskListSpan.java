package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * @since 1.0.1
 */
public class TaskListSpan implements LeadingMarginSpan {

    private final SpannableTheme mTheme;
    private final int mBlockIndent;
    private final int mStart;
    private final boolean mIsDone;

    public TaskListSpan(@NonNull SpannableTheme theme, int blockIndent, int start, boolean isDone) {
        mTheme = theme;
        mBlockIndent = blockIndent;
        mStart = start;
        mIsDone = isDone;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mTheme.getBlockMargin() * mBlockIndent;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        if (!first) {
            return;
        }

        final Drawable drawable = mTheme.getTaskListDrawable();
        if (drawable == null) {
            return;
        }

        final int save = c.save();
        try {

            final int width = mTheme.getBlockMargin();
            final int height = bottom - top;

            final int w = (int) (width * .75F + .5F);
            final int h = (int) (height * .75F + .5F);

            drawable.setBounds(0, 0, w, h);

            if (drawable.isStateful()) {
                final int[] state;
                if (mIsDone) {
                    state = new int[]{android.R.attr.state_checked};
                } else {
                    state = new int[0];
                }
                drawable.setState(state);
            }

            final int l = (width * (mBlockIndent - 1)) + ((width - w) / 2);
            final int t = top + ((height - h) / 2);

            c.translate(l, t);
            drawable.draw(c);

        } finally {
            c.restoreToCount(save);
        }
    }
}
