package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class BlockQuoteSpan implements LeadingMarginSpan {

    private final SpannableTheme theme;
    private final Rect rect = ObjectsPool.rect();
    private final Paint paint = ObjectsPool.paint();
    private final int indent;

    public BlockQuoteSpan(@NonNull SpannableTheme theme, int indent) {
        this.theme = theme;
        this.indent = indent;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return theme.getBlockMargin();
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

        final int width = theme.getBlockQuoteWidth();

        theme.applyBlockQuoteStyle(paint);

        final int left = theme.getBlockMargin() * (indent - 1);
        rect.set(left, top, left + width, bottom);

        c.drawRect(rect, paint);
    }
}
