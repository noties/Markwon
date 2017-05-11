package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ReplacementSpan;

@SuppressWarnings("WeakerAccess")
public class DrawableSpan extends ReplacementSpan {

    @IntDef({ ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER })
    @interface Alignment {}

    public static final int ALIGN_BOTTOM = 0;
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_CENTER = 2;

    private final Drawable drawable;
    private final int alignment;

    public DrawableSpan(@NonNull Drawable drawable) {
        this(drawable, ALIGN_BOTTOM);
    }

    public DrawableSpan(@NonNull Drawable drawable, @Alignment int alignment) {
        this.drawable = drawable;
        this.alignment = alignment;

        // additionally set intrinsic bounds if empty
        final Rect rect = drawable.getBounds();
        if (rect.isEmpty()) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
    }

    @Override
    public int getSize(
            @NonNull Paint paint,
            CharSequence text,
            @IntRange(from = 0) int start,
            @IntRange(from = 0) int end,
            @Nullable Paint.FontMetricsInt fm) {

        final Rect rect = drawable.getBounds();

        if (fm != null) {
            fm.ascent = -rect.bottom;
            fm.descent = 0;

            fm.top = fm.ascent;
            fm.bottom = 0;
        }

        return rect.right;
    }

    @Override
    public void draw(
            @NonNull Canvas canvas,
            CharSequence text,
            @IntRange(from = 0) int start,
            @IntRange(from = 0) int end,
            float x,
            int top,
            int y,
            int bottom,
            @NonNull Paint paint) {

        final Drawable drawable = this.drawable;

        final int b = bottom - drawable.getBounds().bottom;

        final int save = canvas.save();
        try {
            final int translationY;
            if (ALIGN_CENTER == alignment) {
                translationY = (int) (b / 2.F + .5F);
            } else if (ALIGN_BASELINE == alignment) {
                translationY = b - paint.getFontMetricsInt().descent;
            } else {
                translationY = b;
            }
            canvas.translate(x, translationY);
            drawable.draw(canvas);
        } finally {
            canvas.restoreToCount(save);
        }
    }

    public Drawable getDrawable() {
        return drawable;
    }
}
