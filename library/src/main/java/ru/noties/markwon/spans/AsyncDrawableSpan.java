package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ReplacementSpan;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("WeakerAccess")
public class AsyncDrawableSpan extends ReplacementSpan {

    @IntDef({ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER})
    @Retention(RetentionPolicy.SOURCE)
    @interface Alignment {
    }

    public static final int ALIGN_BOTTOM = 0;
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_CENTER = 2; // will only center if drawable height is less than mText line height

    private final SpannableTheme mTheme;
    private final AsyncDrawable mDrawable;
    private final int mAlignment;
    private final boolean mReplacementTextIsLink;

    private int mLastKnownDrawX;
    private int mLastKnownDrawY;

    public AsyncDrawableSpan(@NonNull SpannableTheme theme, @NonNull AsyncDrawable drawable) {
        this(theme, drawable, ALIGN_BOTTOM);
    }

    public AsyncDrawableSpan(
            @NonNull SpannableTheme theme,
            @NonNull AsyncDrawable drawable,
            @Alignment int alignment) {
        this(theme, drawable, alignment, false);
    }

    public AsyncDrawableSpan(
            @NonNull SpannableTheme theme,
            @NonNull AsyncDrawable drawable,
            @Alignment int alignment,
            boolean replacementTextIsLink) {
        mTheme = theme;
        mDrawable = drawable;
        mAlignment = alignment;
        mReplacementTextIsLink = replacementTextIsLink;

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

        // if we have no async drawable result - we will just render mText

        final int size;

        if (mDrawable.hasResult()) {

            final Rect rect = mDrawable.getBounds();

            if (fm != null) {
                fm.ascent = -rect.bottom;
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }

            size = rect.right;

        } else {

            // we will apply style here in case if theme modifies textSize or style (affects metrics)
            if (mReplacementTextIsLink) {
                mTheme.applyLinkStyle(paint);
            }

            // NB, no specific mText handling (no new lines, etc)
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

        mLastKnownDrawX = (int) (x + .5F);
        mLastKnownDrawY = y;

        final AsyncDrawable drawable = mDrawable;

        if (drawable.hasResult()) {

            final int b = bottom - drawable.getBounds().bottom;

            final int save = canvas.save();
            try {
                final int translationY;
                if (ALIGN_CENTER == mAlignment) {
                    translationY = b - ((bottom - top - drawable.getBounds().height()) / 2);
                } else if (ALIGN_BASELINE == mAlignment) {
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

            final float textY = CanvasUtils.textCenterY(top, bottom, paint);
            if (mReplacementTextIsLink) {
                mTheme.applyLinkStyle(paint);
            }

            // NB, no specific mText handling (no new lines, etc)
            canvas.drawText(text, start, end, x, textY, paint);
        }
    }

    public AsyncDrawable getDrawable() {
        return mDrawable;
    }

    public int lastKnownDrawX() {
        return mLastKnownDrawX;
    }

    public int lastKnownDrawY() {
        return mLastKnownDrawY;
    }
}
