package io.noties.markwon.ext.latex;

import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.image.AsyncDrawable;

/**
 * @since 4.3.0
 */
class JLatexInlineAsyncDrawableSpan extends JLatexAsyncDrawableSpan {

    private final AsyncDrawable drawable;

    JLatexInlineAsyncDrawableSpan(@NonNull MarkwonTheme theme, @NonNull JLatextAsyncDrawable drawable, @ColorInt int color) {
        super(theme, drawable, color);
        this.drawable = drawable;
    }

    @Override
    public int getSize(
            @NonNull Paint paint,
            CharSequence text,
            @IntRange(from = 0) int start,
            @IntRange(from = 0) int end,
            @Nullable Paint.FontMetricsInt fm) {

        // if we have no async drawable result - we will just render text

        final int size;

        if (drawable.hasResult()) {

            final Rect rect = drawable.getBounds();

            if (fm != null) {
                final int half = rect.bottom / 2;
                fm.ascent = -half;
                fm.descent = half;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }

            size = rect.right;

        } else {

            // NB, no specific text handling (no new lines, etc)
            size = (int) (paint.measureText(text, start, end) + .5F);
        }

        return size;
    }
}
