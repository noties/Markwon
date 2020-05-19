package io.noties.markwon.image;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.style.ReplacementSpan;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.utils.SpanUtils;

@SuppressWarnings("WeakerAccess")
public class AsyncDrawableSpan extends ReplacementSpan {

    @IntDef({ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER})
    @Retention(RetentionPolicy.SOURCE)
    @interface Alignment {
    }

    public static final int ALIGN_BOTTOM = 0;
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_CENTER = 2; // will only center if drawable height is less than text line height

    private final MarkwonTheme theme;
    private final AsyncDrawable drawable;
    private final int alignment;
    private final boolean replacementTextIsLink;

    public AsyncDrawableSpan(
            @NonNull MarkwonTheme theme,
            @NonNull AsyncDrawable drawable,
            @Alignment int alignment,
            boolean replacementTextIsLink) {
        this.theme = theme;
        this.drawable = drawable;
        this.alignment = alignment;
        this.replacementTextIsLink = replacementTextIsLink;

        // @since 4.2.1 we do not set intrinsic bounds
        //  at this point they will always be 0,0-1,1, but this
        //  will trigger another invalidation when we will have bounds
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

            // we will apply style here in case if theme modifies textSize or style (affects metrics)
            if (replacementTextIsLink) {
                theme.applyLinkStyle(paint);
            }

            // NB, no specific text handling (no new lines, etc)
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

        // @since 4.4.0 use SpanUtils instead of `canvas.getWidth`
        drawable.initWithKnownDimensions(
                SpanUtils.width(canvas, text),
                paint.getTextSize()
        );

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

            // will it make sense to have additional background/borders for an image replacement?
            // let's focus on main functionality and then think of it

            final float textY = textCenterY(top, bottom, paint);
            if (replacementTextIsLink) {
                theme.applyLinkStyle(paint);
            }

            // NB, no specific text handling (no new lines, etc)
            canvas.drawText(text, start, end, x, textY, paint);
        }
    }

    @NonNull
    public AsyncDrawable getDrawable() {
        return drawable;
    }

    private static float textCenterY(int top, int bottom, @NonNull Paint paint) {
        // @since 1.1.1 it's `top +` and not `bottom -`
        return (int) (top + ((bottom - top) / 2) - ((paint.descent() + paint.ascent()) / 2.F + .5F));
    }
}
