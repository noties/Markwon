package io.noties.markwon.ext.tables;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.core.spans.TextLayoutSpan;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.AsyncDrawableSpan;
import io.noties.markwon.utils.LeadingMarginUtils;
import io.noties.markwon.utils.SpanUtils;

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

        final int alignment;
        final CharSequence text;

        public Cell(@Alignment int alignment, CharSequence text) {
            this.alignment = alignment;
            this.text = text;
        }

        @Alignment
        public int alignment() {
            return alignment;
        }

        public CharSequence text() {
            return text;
        }

        @NonNull
        @Override
        public String toString() {
            return "Cell{" +
                    "alignment=" + alignment +
                    ", text=" + text +
                    '}';
        }
    }

    private final TableTheme theme;
    private final List<Cell> cells;
    private final List<Layout> layouts;
    private final TextPaint textPaint;
    private final boolean header;
    private final boolean odd;

    private final Rect rect = new Rect();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int width;
    private int height;
    private Invalidator invalidator;

    public TableRowSpan(
            @NonNull TableTheme theme,
            @NonNull List<Cell> cells,
            boolean header,
            boolean odd) {
        this.theme = theme;
        this.cells = cells;
        this.layouts = new ArrayList<>(cells.size());
        this.textPaint = new TextPaint();
        this.header = header;
        this.odd = odd;
    }

    @Override
    public int getSize(
            @NonNull Paint paint,
            CharSequence text,
            @IntRange(from = 0) int start,
            @IntRange(from = 0) int end,
            @Nullable Paint.FontMetricsInt fm) {

        // it's our absolute requirement to have width of the canvas here... because, well, it changes
        // the way we draw text. So, if we do not know the width of canvas we cannot correctly measure our text

        if (layouts.size() > 0) {

            if (fm != null) {

                int max = 0;
                for (Layout layout : layouts) {
                    final int height = layout.getHeight();
                    if (height > max) {
                        max = height;
                    }
                }

                // we store actual height
                height = max;

                // but apply height with padding
                final int padding = theme.tableCellPadding() * 2;

                fm.ascent = -(max + padding);
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }
        }

        return width;
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
            @NonNull Paint p) {

        final int spanWidth = SpanUtils.width(canvas, text);
        if (recreateLayouts(spanWidth)) {
            width = spanWidth;
            // @since 4.3.1 it's important to cast to TextPaint in order to display links, etc
            if (p instanceof TextPaint) {
                // there must be a reason why this method receives Paint instead of TextPaint...
                textPaint.set((TextPaint) p);
            } else {
                textPaint.set(p);
            }
            makeNewLayouts();
        }

        int maxHeight = 0;

        final int padding = theme.tableCellPadding();

        final int size = layouts.size();

        final int w = cellWidth(size);

        // @since 4.6.0 roundingDiff to offset last vertical border
        final int roundingDiff = w - (width / size);

        // @since 1.1.1
        // draw backgrounds
        {
            if (header) {
                theme.applyTableHeaderRowStyle(paint);
            } else if (odd) {
                theme.applyTableOddRowStyle(paint);
            } else {
                // even
                theme.applyTableEvenRowStyle(paint);
            }

            // if present (0 is transparent)
            if (paint.getColor() != 0) {
                final int save = canvas.save();
                try {
                    rect.set(0, 0, width, bottom - top);
                    canvas.translate(x, top);
                    canvas.drawRect(rect, paint);
                } finally {
                    canvas.restoreToCount(save);
                }
            }
        }

        // @since 1.1.1 reset after applying background color
        // as background changes color attribute and if not specific tableBorderColor
        // is specified then after this row all borders will have color of this row (plus alpha)
        paint.set(p);
        theme.applyTableBorderStyle(paint);

        final int borderWidth = theme.tableBorderWidth(paint);
        final boolean drawBorder = borderWidth > 0;

        // why divided by 4 gives a more or less good result is still not clear (shouldn't it be 2?)
        final int heightDiff = (bottom - top - height) / 4;

        // required for borderTop calculation
        final boolean isFirstTableRow;

        // @since 4.3.1
        if (drawBorder) {
            boolean first = false;
            // only if first draw the line
            {
                final Spanned spanned = (Spanned) text;
                final TableSpan[] spans = spanned.getSpans(start, end, TableSpan.class);
                if (spans != null && spans.length > 0) {
                    final TableSpan span = spans[0];
                    if (LeadingMarginUtils.selfStart(start, text, span)) {
                        first = true;
                        rect.set((int) x, top, width, top + borderWidth);
                        canvas.drawRect(rect, paint);
                    }
                }
            }

            // draw the line at the bottom
            rect.set((int) x, bottom - borderWidth, width, bottom);
            canvas.drawRect(rect, paint);

            isFirstTableRow = first;
        } else {
            isFirstTableRow = false;
        }

        final int borderWidthHalf = borderWidth / 2;

        // to NOT overlap borders inset top and bottom
        final int borderTop = isFirstTableRow ? borderWidth : 0;
        final int borderBottom = bottom - top - borderWidth;

        Layout layout;
        for (int i = 0; i < size; i++) {
            layout = layouts.get(i);
            final int save = canvas.save();
            try {

                canvas.translate(x + (i * w), top);

                // @since 4.3.1
                if (drawBorder) {
                    // first vertical border will have full width (it cannot exceed canvas)
                    if (i == 0) {
                        rect.set(0, borderTop, borderWidth, borderBottom);
                    } else {
                        rect.set(-borderWidthHalf, borderTop, borderWidthHalf, borderBottom);
                    }

                    canvas.drawRect(rect, paint);

                    if (i == (size - 1)) {
                        // @since 4.6.0 subtract rounding offset for the last vertical divider
                        rect.set(
                                w - borderWidth - roundingDiff,
                                borderTop,
                                w - roundingDiff,
                                borderBottom
                        );
                        canvas.drawRect(rect, paint);
                    }
                }

                canvas.translate(padding, padding + heightDiff);
                layout.draw(canvas);

                if (layout.getHeight() > maxHeight) {
                    maxHeight = layout.getHeight();
                }

            } finally {
                canvas.restoreToCount(save);
            }
        }

        if (height != maxHeight) {
            if (invalidator != null) {
                invalidator.invalidate();
            }
        }
    }

    private boolean recreateLayouts(int newWidth) {
        return width != newWidth;
    }

    private void makeNewLayouts() {

        textPaint.setFakeBoldText(header);

        final int columns = cells.size();
        final int padding = theme.tableCellPadding() * 2;
        final int w = cellWidth(columns) - padding;

        this.layouts.clear();

        for (int i = 0, size = cells.size(); i < size; i++) {
            makeLayout(i, w, cells.get(i));
        }
    }

    private void makeLayout(final int index, final int width, @NonNull final Cell cell) {

        final Runnable recreate = new Runnable() {
            @Override
            public void run() {
                final Invalidator invalidator = TableRowSpan.this.invalidator;
                if (invalidator != null) {
                    layouts.remove(index);
                    makeLayout(index, width, cell);
                    invalidator.invalidate();
                }
            }
        };

        final Spannable spannable;

        if (cell.text instanceof Spannable) {
            spannable = (Spannable) cell.text;
        } else {
            spannable = new SpannableString(cell.text);
        }

        final Layout layout = new StaticLayout(
                spannable,
                textPaint,
                width,
                alignment(cell.alignment),
                1.0F,
                0.0F,
                false
        );

        // @since 4.4.0
        TextLayoutSpan.applyTo(spannable, layout);

        // @since 4.4.0
        scheduleAsyncDrawables(spannable, recreate);

        layouts.add(index, layout);
    }

    private void scheduleAsyncDrawables(@NonNull Spannable spannable, @NonNull final Runnable recreate) {

        final AsyncDrawableSpan[] spans = spannable.getSpans(0, spannable.length(), AsyncDrawableSpan.class);
        if (spans != null
                && spans.length > 0) {

            for (AsyncDrawableSpan span : spans) {

                final AsyncDrawable drawable = span.getDrawable();

                // it is absolutely crucial to check if drawable is already attached,
                //  otherwise we would end up with a loop
                if (drawable.isAttached()) {
                    continue;
                }

                drawable.setCallback2(new CallbackAdapter() {
                    @Override
                    public void invalidateDrawable(@NonNull Drawable who) {
                        recreate.run();
                    }
                });
            }
        }
    }

    /**
     * Obtain Layout given horizontal offset. Primary usage target - MovementMethod
     *
     * @since 4.6.0
     */
    @Nullable
    public Layout findLayoutForHorizontalOffset(int x) {
        final int size = layouts.size();
        final int w = cellWidth(size);
        final int i = x / w;
        if (i >= size) {
            return null;
        }
        return layouts.get(i);
    }

    /**
     * @since 4.6.0
     */
    public int cellWidth() {
        return cellWidth(layouts.size());
    }

    // @since 4.6.0
    protected int cellWidth(int size) {
        return (int) (1F * width / size + 0.5F);
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

    public void invalidator(@Nullable Invalidator invalidator) {
        this.invalidator = invalidator;
    }

    private static abstract class CallbackAdapter implements Drawable.Callback {
        @Override
        public void invalidateDrawable(@NonNull Drawable who) {

        }

        @Override
        public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

        }

        @Override
        public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

        }
    }
}
