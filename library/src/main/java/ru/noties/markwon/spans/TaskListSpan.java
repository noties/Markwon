package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class TaskListSpan implements LeadingMarginSpan {

    private final SpannableTheme theme;
    private final int blockIndent;
    private final int start;
    private final boolean isDone;

    public TaskListSpan(@NonNull SpannableTheme theme, int blockIndent, int start, boolean isDone) {
        this.theme = theme;
        this.blockIndent = blockIndent;
        this.start = start;
        this.isDone = isDone;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return theme.getBlockMargin();
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        if (this.start != start) {
            return;
        }

        final int save = c.save();
        try {

            final int width = theme.getBlockMargin();
            final int height = bottom - top;

            final Drawable drawable = theme.getTaskListDrawable();

            final int w = (int) (width * .75F + .5F);
            final int h = (int) (height * .75F + .5F);

            drawable.setBounds(0, 0, w, h);

            if (drawable.isStateful()) {
                final int[] state;
                if (isDone) {
                    state = new int[]{android.R.attr.state_checked};
                } else {
                    state = new int[0];
                }
                drawable.setState(state);
            }

            final int l = (width * (blockIndent - 1)) + ((width - w) / 2);
            final int t = top + ((height - h) / 2);

            c.translate(l, t);
            drawable.draw(c);

        } finally {
            c.restoreToCount(save);
        }
    }
}
