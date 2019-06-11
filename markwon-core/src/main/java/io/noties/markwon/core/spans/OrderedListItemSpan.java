package io.noties.markwon.core.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.utils.LeadingMarginUtils;

public class OrderedListItemSpan implements LeadingMarginSpan {

    /**
     * Process supplied `text` argument and supply TextView paint to all OrderedListItemSpans
     * in order for them to measure number.
     * <p>
     * NB, this method must be called <em>before</em> setting text to a TextView (`TextView#setText`
     * internally can trigger new Layout creation which will ask for leading margins right away)
     *
     * @param textView to which markdown will be applied
     * @param text     parsed markdown to process
     * @since 2.0.1
     */
    public static void measure(@NonNull TextView textView, @NonNull CharSequence text) {

        if (!(text instanceof Spanned)) {
            // nothing to do here
            return;
        }

        final OrderedListItemSpan[] spans = ((Spanned) text).getSpans(
                0,
                text.length(),
                OrderedListItemSpan.class);

        if (spans != null) {
            final TextPaint paint = textView.getPaint();
            for (OrderedListItemSpan span : spans) {
                span.margin = (int) (paint.measureText(span.number) + .5F);
            }
        }
    }

    private final MarkwonTheme theme;
    private final String number;
    private final Paint paint = ObjectsPool.paint();

    // we will use this variable to check if our order number text exceeds block margin,
    // so we will use it instead of block margin
    // @since 1.0.3
    private int margin;

    public OrderedListItemSpan(
            @NonNull MarkwonTheme theme,
            @NonNull String number
    ) {
        this.theme = theme;
        this.number = number;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        // @since 2.0.1 we return maximum value of both (now we should measure number before)
        return Math.max(margin, theme.getBlockMargin());
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        // if there was a line break, we don't need to draw anything
        if (!first
                || !LeadingMarginUtils.selfStart(start, text, this)) {
            return;
        }

        paint.set(p);

        theme.applyListItemStyle(paint);

        // if we could force usage of #measure method then we might want skip this measuring here
        // but this won't hold against new values that a TextView can receive (new text size for
        // example...)
        final int numberWidth = (int) (paint.measureText(number) + .5F);

        // @since 1.0.3
        int width = theme.getBlockMargin();
        if (numberWidth > width) {
            // let's keep this logic here in case a user decided not to call #measure and is fine
            // with current implementation
            width = numberWidth;
            margin = numberWidth;
        } else {
            margin = 0;
        }

        final int left;
        if (dir > 0) {
            left = x + (width * dir) - numberWidth;
        } else {
            left = x + (width * dir) + (width - numberWidth);
        }

        // @since 1.1.1 we are using `baseline` argument to position text
        c.drawText(number, left, baseline, paint);
    }
}
