package io.noties.markwon.ext.latex;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import org.scilab.forge.jlatexmath.TeXIcon;

import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.image.AsyncDrawableSpan;
import ru.noties.jlatexmath.JLatexMathDrawable;
import ru.noties.jlatexmath.awt.Color;

/**
 * @since 4.3.0
 */
public class JLatexAsyncDrawableSpan extends AsyncDrawableSpan {

    private final JLatextAsyncDrawable drawable;
    private final int color;
    private boolean appliedTextColor;

    public JLatexAsyncDrawableSpan(
            @NonNull MarkwonTheme theme,
            @NonNull JLatextAsyncDrawable drawable,
            @ColorInt int color) {
        super(theme, drawable, ALIGN_CENTER, false);
        this.drawable = drawable;
        this.color = color;
        // if color is not 0 -> then no need to apply text color
        this.appliedTextColor = color != 0;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        if (!appliedTextColor && drawable.hasResult()) {
            // it is important to check for type (in case of an error, or custom placeholder or whatever
            //  this result can be of other type)
            final Drawable drawableResult = drawable.getResult();
            if (drawableResult instanceof JLatexMathDrawable) {
                final JLatexMathDrawable result = (JLatexMathDrawable) drawableResult;
                final TeXIcon icon = result.icon();
                icon.setForeground(new Color(paint.getColor()));
                appliedTextColor = true;
            }
        }
        super.draw(canvas, text, start, end, x, top, y, bottom, paint);
    }

    @NonNull
    public JLatextAsyncDrawable drawable() {
        return drawable;
    }

    @ColorInt
    public int color() {
        return color;
    }
}
