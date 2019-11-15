package io.noties.markwon.core.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.utils.LeadingMarginUtils;

public class HeadingSpan extends MetricAffectingSpan implements LeadingMarginSpan {

    private final MarkwonTheme theme;
    private final Rect rect = ObjectsPool.rect();
    private final Paint paint = ObjectsPool.paint();
    private final int level;

    public HeadingSpan(@NonNull MarkwonTheme theme, @IntRange(from = 1, to = 6) int level) {
        this.theme = theme;
        this.level = level;
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
        theme.applyHeadingTextStyle(paint, level);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        // no margin actually, but we need to access Canvas to draw break
        return 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        if ((level == 1 || level == 2)
                && LeadingMarginUtils.selfEnd(end, text, this)) {

            paint.set(p);

            theme.applyHeadingBreakStyle(paint);

            final float height = paint.getStrokeWidth();

            if (height > .0F) {

                final int b = (int) (bottom - height + .5F);

                final int left;
                final int right;
                if (dir > 0) {
                    left = x;
                    right = c.getWidth();
                } else {
                    left = x - c.getWidth();
                    right = x;
                }

                rect.set(left, b, right, bottom);
                c.drawRect(rect, paint);
            }
        }
    }

    /**
     * @since 4.2.0
     */
    public int getLevel() {
        return level;
    }
}
