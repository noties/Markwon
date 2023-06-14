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
                Paint.FontMetricsInt originFont = paint.getFontMetricsInt();
                int diff = rect.height() - (originFont.descent - originFont.ascent);

                fm.descent = originFont.descent + diff / 2;
                fm.ascent = fm.descent - rect.height();
                fm.top = fm.ascent;
                fm.bottom = fm.descent;
            }

            size = rect.right;

        } else {

            // NB, no specific text handling (no new lines, etc)
            size = (int) (paint.measureText(text, start, end) + .5F);
        }

        return size;
    }
}
