package io.noties.markwon.ext.tasklist;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import androidx.annotation.NonNull;

import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.utils.LeadingMarginUtils;

/**
 * @since 1.0.1
 */
public class TaskListSpan implements LeadingMarginSpan {

    private static final int[] STATE_CHECKED = new int[]{android.R.attr.state_checked};

    private static final int[] STATE_NONE = new int[0];

    private final MarkwonTheme theme;
    private final Drawable drawable;

    // @since 2.0.1 field is NOT final (to allow mutation)
    private boolean isDone;

    public TaskListSpan(@NonNull MarkwonTheme theme, @NonNull Drawable drawable, boolean isDone) {
        this.theme = theme;
        this.drawable = drawable;
        this.isDone = isDone;
    }

    /**
     * @since 2.0.1
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Update {@link #isDone} property of this span. Please note that this is merely a visual change
     * which is not changing underlying text in any means.
     *
     * @since 2.0.1
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return theme.getBlockMargin();
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        if (!first
                || !LeadingMarginUtils.selfStart(start, text, this)) {
            return;
        }

        final float descent = p.descent();
        final float ascent = p.ascent();

        final int save = c.save();
        try {

            final int width = theme.getBlockMargin();
            final int height = (int) (descent - ascent + 0.5F);

            final int w = (int) (width * .75F + .5F);
            final int h = (int) (height * .75F + .5F);

            drawable.setBounds(0, 0, w, h);

            if (drawable.isStateful()) {
                final int[] state;
                if (isDone) {
                    state = STATE_CHECKED;
                } else {
                    state = STATE_NONE;
                }
                drawable.setState(state);
            }

            final int l;
            if (dir > 0) {
                l = x + ((width - w) / 2);
            } else {
                l = x - ((width - w) / 2) - w;
            }

            final int t = (int) (baseline + ascent + 0.5F) + ((height - h) / 2);

            c.translate(l, t);
            drawable.draw(c);

        } finally {
            c.restoreToCount(save);
        }
    }
}
