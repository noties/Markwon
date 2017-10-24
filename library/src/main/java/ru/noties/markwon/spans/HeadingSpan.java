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

    private final SpannableTheme theme;
    private final Rect rect = ObjectsPool.rect();
    private final Paint paint = ObjectsPool.paint();
    private final int level;
    private final int textLength;

    public HeadingSpan(@NonNull SpannableTheme theme, @IntRange(from = 1, to = 6) int level, @IntRange(from = 0) int textLength) {
        this.theme = theme;
        this.level = level;
        this.textLength = textLength;
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

        if (level == 1
                || level == 2) {

            if ((start + textLength) == end) {
                paint.set(p);

                theme.applyHeadingBreakStyle(paint);

                final float height = paint.getStrokeWidth();
                final int b = (int) (bottom - height + .5F);

                rect.set(x, b, c.getWidth(), bottom);
                c.drawRect(rect, paint);
            }
        }
    }
}
