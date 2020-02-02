package io.noties.markwon.core.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.utils.LeadingMarginUtils;

public class BulletListItemSpan implements LeadingMarginSpan {

    private static final boolean IS_NOUGAT;

    static {
        final int sdk = Build.VERSION.SDK_INT;
        IS_NOUGAT = Build.VERSION_CODES.N == sdk || Build.VERSION_CODES.N_MR1 == sdk;
    }

    private MarkwonTheme theme;

    private final Paint paint = ObjectsPool.paint();
    private final RectF circle = ObjectsPool.rectF();
    private final Rect rectangle = ObjectsPool.rect();

    private final int level;

    public BulletListItemSpan(
            @NonNull MarkwonTheme theme,
            @IntRange(from = 0) int level) {
        this.theme = theme;
        this.level = level;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return theme.getBlockMargin();
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        // if there was a line break, we don't need to draw anything
        if (!first
                || !LeadingMarginUtils.selfStart(start, text, this)) {
            return;
        }

        paint.set(p);

        theme.applyListItemStyle(paint);

        final int save = c.save();
        try {

            final int width = theme.getBlockMargin();

            // @since 1.0.6 we no longer rely on (bottom-top) calculation in order to detect line height
            // it lead to bad rendering as first & last lines received different results even
            // if text size is the same (first line received greater amount and bottom line -> less)
            final int textLineHeight = (int) (paint.descent() - paint.ascent() + .5F);

            final int side = theme.getBulletWidth(textLineHeight);

            final int marginLeft = (width - side) / 2;

            // in order to support RTL
            final int l;
            final int r;
            {
                // @since 4.2.1 to correctly position bullet
                // when nested inside other LeadingMarginSpans (sorry, Nougat)
                if (IS_NOUGAT) {

                    // @since 2.0.2
                    // There is a bug in Android Nougat, when this span receives an `x` that
                    // doesn't correspond to what it should be (text is placed correctly though).
                    // Let's make this a general rule -> manually calculate difference between expected/actual
                    // and add this difference to resulting left/right values. If everything goes well
                    // we do not encounter a bug -> this `diff` value will be 0
                    final int diff;
                    if (dir < 0) {
                        // rtl
                        diff = x - (layout.getWidth() - (width * level));
                    } else {
                        diff = (width * level) - x;
                    }

                    final int left = x + (dir * marginLeft);
                    final int right = left + (dir * side);
                    l = Math.min(left, right) + (dir * diff);
                    r = Math.max(left, right) + (dir * diff);

                } else {
                    if (dir > 0) {
                        l = x + marginLeft;
                    } else {
                        l = x - width + marginLeft;
                    }
                    r = l + side;
                }
            }

            final int t = baseline + (int) ((paint.descent() + paint.ascent()) / 2.F + .5F) - (side / 2);
            final int b = t + side;

            if (level == 0
                    || level == 1) {

                circle.set(l, t, r, b);

                final Paint.Style style = level == 0
                        ? Paint.Style.FILL
                        : Paint.Style.STROKE;
                paint.setStyle(style);

                c.drawOval(circle, paint);
            } else {

                rectangle.set(l, t, r, b);

                paint.setStyle(Paint.Style.FILL);

                c.drawRect(rectangle, paint);
            }

        } finally {
            c.restoreToCount(save);
        }
    }
}
