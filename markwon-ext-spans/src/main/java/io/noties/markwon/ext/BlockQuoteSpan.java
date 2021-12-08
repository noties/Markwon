package io.noties.markwon.;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineHeightSpan;
import io.noties.markwon.utils.ColorUtils;

public class BlockQuoteSpan implements LeadingMarginSpan, LineHeightSpan {
    protected int BLOCK_QUOTE_DEF_COLOR_ALPHA = 100;
    protected static final int BLOCK_QUOTE_WIDTH = 10;
    public static int BLOCK_QUOTE_MARGIN = 100;
    protected int BLOCK_COLOR = Color.LTGRAY;
    private final int VERTICAL_SPACING = 20;
    private final Rect rect = ObjectsPool.rect();
    private final Paint paint = ObjectsPool.paint();

    public BlockQuoteSpan() {

    }

    @Override
    public int getLeadingMargin(boolean first) {
        return BLOCK_QUOTE_MARGIN;
    }

    @Override
    public void drawLeadingMargin(
            Canvas c,
            Paint p,
            int x,
            int dir,
            int top,
            int baseline,
            int bottom,
            CharSequence text,
            int start,
            int end,
            boolean first,
            Layout layout) {

        final int width = BLOCK_QUOTE_WIDTH;

        paint.set(p);

        applyBlockQuoteStyle(paint);

        rect.set(x, top, x + dir * width, bottom);

        c.drawRect(rect, paint);
    }

    public void applyBlockQuoteStyle(Paint paint) {
        final int color = ColorUtils.applyAlpha(BLOCK_COLOR, BLOCK_QUOTE_DEF_COLOR_ALPHA);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {
        final Spanned txt = (Spanned)text;
        final int spanEnd = txt.getSpanEnd(this);
        final int spanStart = txt.getSpanStart(this);

        // add top spacing to first line
        if (start == spanStart) {
            fm.ascent -= VERTICAL_SPACING;
            fm.top -= VERTICAL_SPACING;
        }

        // add bottom spacing to last line
        if (Math.abs(spanEnd - end) <= 1) {
            fm.descent += VERTICAL_SPACING;
            fm.bottom += VERTICAL_SPACING;
        }
    }
}

