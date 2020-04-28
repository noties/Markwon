package io.noties.markwon.spans.better;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.LineBackgroundSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import io.noties.markwon.core.spans.TextViewSpan;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Credit goes to [Romain Guy](https://github.com/romainguy/elegant-underline)
 *
 * @since $nap;
 */
public class BetterUnderlineSpan implements LineBackgroundSpan {

    public enum Type {
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        PATH,
        REGION
    }

    private static final float UNDERLINE_CLEAR_GAP = 5.5F;

    private final Path underline = new Path();
    private final Path outline = new Path();
    private final Paint stroke = new Paint();
    private final Path strokedOutline = new Path();
    private char[] chars;

    BetterUnderlineSpan() {
        stroke.setStyle(Paint.Style.FILL_AND_STROKE);
        stroke.setStrokeCap(Paint.Cap.BUTT);
    }

    @Override
    public void drawBackground(
            Canvas c,
            Paint p,
            int left,
            int right,
            int top,
            int baseline,
            int bottom,
            CharSequence text,
            int start,
            int end,
            int lnum
    ) {
        final Spanned spanned = (Spanned) text;
        final TextView textView = TextViewSpan.textViewOf(spanned);

        if (textView == null) {
            // no, cannot do it, the whole text will be changed
//                p.setUnderlineText(true);
            return;
        }

        final Layout layout = textView.getLayout();

        final int selfStart = spanned.getSpanStart(this);
        final int selfEnd = spanned.getSpanEnd(this);

        // TODO: also doesn't mean that it is last line, imagine text after span is ended
        final boolean isLastLine = end == selfEnd || (selfEnd == (end - 1));

        final int s = max(selfStart, start);

        // e - 1, but only if not last?
        // oh... layout line count != span lines..
        final int e = min(selfEnd, end) - (isLastLine ? 0 : 1);

        final int l = (int) (layout.getPrimaryHorizontal(s) + .5F);
        final int r = (int) (layout.getPrimaryHorizontal(e) + .5F);
        final int b = getLineBottom(layout, lnum, isLastLine);

        final float density = textView.getResources().getDisplayMetrics().density;

        underline.rewind();
        // TODO: proper baseline
//            underline.addRect(
//                    l, b - (1.8F * density),
//                    r, b,
//                    Path.Direction.CW
//
//            );

        // TODO: this must be configured somehow...
        final int diff = (int) (p.descent() / 2F + .5F);

        underline.addRect(
                l, baseline + diff,
                r, baseline + diff + (density * 0.8F),
                Path.Direction.CW
        );


        outline.rewind();

        // reallocate only if less, otherwise re-use and then send actual indexes
        // TODO: would this return proper array for the last line?!
        chars = new char[e - s];
        TextUtils.getChars(spanned, s, e, chars, 0);
        p.getTextPath(
                chars,
                0, (e - s),
                l, baseline,
                outline
        );

        final Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        c.drawPath(outline, paint);

        outline.op(underline, Path.Op.INTERSECT);

        strokedOutline.rewind();
        stroke.setStrokeWidth(UNDERLINE_CLEAR_GAP * density);
        stroke.getFillPath(outline, strokedOutline);

        underline.op(strokedOutline, Path.Op.DIFFERENCE);

        c.drawPath(underline, p);
    }

    private static final float DEFAULT_EXTRA = 0F;
    private static final float DEFAULT_MULTIPLIER = 1F;

    private static int getLineBottom(@NonNull Layout layout, int line, boolean isLastLine) {

        final int bottom = layout.getLineBottom(line);
        final boolean lastLineSpacingNotAdded = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // TODO: layout line count != span occupied lines
//            final boolean isLastLine = line == layout.getLineCount() - 1;

        final int lineBottom;
        final float lineSpacingExtra = layout.getSpacingAdd();
        final float lineSpacingMultiplier = layout.getSpacingMultiplier();

        // simplified check
        final boolean hasLineSpacing = lineSpacingExtra != DEFAULT_EXTRA
                || lineSpacingMultiplier != DEFAULT_MULTIPLIER;

        if (!hasLineSpacing
                || (isLastLine && lastLineSpacingNotAdded)) {
            lineBottom = bottom;
        } else {
            final float extra;
            if (Float.compare(DEFAULT_MULTIPLIER, lineSpacingMultiplier) != 0) {
                final int lineHeight = getLineHeight(layout, line);
                extra = lineHeight -
                        ((lineHeight - lineSpacingExtra) / lineSpacingMultiplier);
            } else {
                extra = lineSpacingExtra;
            }
            lineBottom = (int) (bottom - extra + .5F);
        }

        if (isLastLine) {
            return lineBottom - layout.getBottomPadding();
        }

        return lineBottom;
    }

    private static int getLineHeight(@NonNull Layout layout, int line) {
        return layout.getLineTop(line + 1) - layout.getLineTop(line);
    }
}
