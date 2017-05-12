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
public class AsyncDrawableSpan extends ReplacementSpan {

    @IntDef({ ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER })
    @interface Alignment {}

    public static final int ALIGN_BOTTOM = 0;
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_CENTER = 2; // will only center if drawable height is less than text line height

    private final AsyncDrawable drawable;
    private final int alignment;

    public AsyncDrawableSpan(@NonNull AsyncDrawable drawable) {
        this(drawable, ALIGN_BOTTOM);
    }

    public AsyncDrawableSpan(@NonNull AsyncDrawable drawable, @Alignment int alignment) {
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

        // if we have no async drawable result - we will just render text

        final int size;

        if (drawable.hasResult()) {

            final Rect rect = drawable.getBounds();

            if (fm != null) {
                fm.ascent = -rect.bottom;
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }

            size = rect.right;

        } else {

            size = (int) (paint.measureText(text, start, end) + .5F);

        }

        return size;
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

        final AsyncDrawable drawable = this.drawable;

        if (drawable.hasResult()) {

            final int b = bottom - drawable.getBounds().bottom;

            final int save = canvas.save();
            try {
                final int translationY;
                if (ALIGN_CENTER == alignment) {
                    translationY = b - ((bottom - top - drawable.getBounds().height()) / 2);
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
        } else {

            final int textY = (int) (bottom - ((bottom - top) / 2) - ((paint.descent() + paint.ascent()) / 2.F + .5F));
            canvas.drawText(text, start, end, x, textY, paint);
        }
    }

    public AsyncDrawable getDrawable() {
        return drawable;
    }
}
