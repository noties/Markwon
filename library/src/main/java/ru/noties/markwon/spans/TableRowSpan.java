package ru.noties.markwon.spans;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class TableRowSpan extends ReplacementSpan {

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;

    @IntDef(value = {ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Alignment {
    }

    public interface Invalidator {
        void invalidate();
    }

    public static class Cell {

        final int mAlignment;
        final CharSequence mText;

        public Cell(@Alignment int alignment, CharSequence text) {
            mAlignment = alignment;
            mText = text;
        }

        @Alignment
        public int alignment() {
            return mAlignment;
        }

        public CharSequence text() {
            return mText;
        }

        @Override
        public String toString() {
            return "Cell{" +
                    "mAlignment=" + mAlignment +
                    ", mText=" + mText +
                    '}';
        }
    }

    private final SpannableTheme mTheme;
    private final List<Cell> mCells;
    private final List<StaticLayout> mLayouts;
    private final TextPaint mTextPaint;
    private final boolean mHeader;
    private final boolean mOdd;

    private final Rect mRect = ObjectsPool.rect();
    private final Paint mPaint = ObjectsPool.paint();

    private int mWidth;
    private int mHeight;
    private Invalidator mInvalidator;

    public TableRowSpan(
            @NonNull SpannableTheme theme,
            @NonNull List<Cell> cells,
            boolean header,
            boolean odd) {
        mTheme = theme;
        mCells = cells;
        mLayouts = new ArrayList<>(cells.size());
        mTextPaint = new TextPaint();
        mHeader = header;
        mOdd = odd;
    }

    @Override
    public int getSize(
            @NonNull Paint paint,
            CharSequence text,
            @IntRange(from = 0) int start,
            @IntRange(from = 0) int end,
            @Nullable Paint.FontMetricsInt fm) {

        // it's our absolute requirement to have width of the canvas here... because, well, it changes
        // the way we draw mText. So, if we do not know the width of canvas we cannot correctly measure our mText

        if (mLayouts.size() > 0) {

            if (fm != null) {

                int max = 0;
                for (StaticLayout layout : mLayouts) {
                    final int height = layout.getHeight();
                    if (height > max) {
                        max = height;
                    }
                }

                // we store actual height
                mHeight = max;

                // but apply height with padding
                final int padding = mTheme.tableCellPadding() * 2;

                fm.ascent = -(max + padding);
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }
        }

        return mWidth;
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

        if (recreateLayouts(canvas.getWidth())) {
            mWidth = canvas.getWidth();
            mTextPaint.set(paint);
            makeNewLayouts();
        }

        int maxHeight = 0;

        final int padding = mTheme.tableCellPadding();

        final int size = mLayouts.size();

        final int w = mWidth / size;

        // feels like magic...
        final int heightDiff = (bottom - top - mHeight) / 4;

        if (mOdd) {
            final int save = canvas.save();
            try {
                mRect.set(0, 0, mWidth, bottom - top);
                mTheme.applyTableOddRowStyle(mPaint);
                canvas.translate(x, top - heightDiff);
                canvas.drawRect(mRect, mPaint);
            } finally {
                canvas.restoreToCount(save);
            }
        }

        mRect.set(0, 0, w, bottom - top);

        mTheme.applyTableBorderStyle(mPaint);

        StaticLayout layout;
        for (int i = 0; i < size; i++) {
            layout = mLayouts.get(i);
            final int save = canvas.save();
            try {

                canvas.translate(x + (i * w), top - heightDiff);
                canvas.drawRect(mRect, mPaint);

                canvas.translate(padding, padding + heightDiff);
                layout.draw(canvas);

                if (layout.getHeight() > maxHeight) {
                    maxHeight = layout.getHeight();
                }

            } finally {
                canvas.restoreToCount(save);
            }
        }

        if (mHeight != maxHeight) {
            if (mInvalidator != null) {
                mInvalidator.invalidate();
            }
        }
    }

    private boolean recreateLayouts(int newWidth) {
        return mWidth != newWidth;
    }

    private void makeNewLayouts() {

        mTextPaint.setFakeBoldText(mHeader);

        final int columns = mCells.size();
        final int padding = mTheme.tableCellPadding() * 2;
        final int w = (mWidth / columns) - padding;

        mLayouts.clear();
        Cell cell;
        StaticLayout layout;
        for (int i = 0, size = mCells.size(); i < size; i++) {
            cell = mCells.get(i);
            layout = new StaticLayout(
                    cell.mText,
                    mTextPaint,
                    w,
                    alignment(cell.mAlignment),
                    1.F,
                    .0F,
                    false
            );
            mLayouts.add(layout);
        }
    }

    @SuppressLint("SwitchIntDef")
    private static Layout.Alignment alignment(@Alignment int alignment) {
        final Layout.Alignment out;
        switch (alignment) {
            case ALIGN_CENTER:
                out = Layout.Alignment.ALIGN_CENTER;
                break;
            case ALIGN_RIGHT:
                out = Layout.Alignment.ALIGN_OPPOSITE;
                break;
            default:
                out = Layout.Alignment.ALIGN_NORMAL;
                break;
        }
        return out;
    }

    public TableRowSpan invalidator(Invalidator invalidator) {
        mInvalidator = invalidator;
        return this;
    }
}
