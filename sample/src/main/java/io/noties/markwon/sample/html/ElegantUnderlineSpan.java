package io.noties.markwon.sample.html;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.LineBackgroundSpan;
import android.text.style.MetricAffectingSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;

import io.noties.markwon.core.spans.TextLayoutSpan;
import io.noties.markwon.core.spans.TextViewSpan;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Credit goes to [Romain Guy](https://github.com/romainguy/elegant-underline)
 * <p>
 * Failed attempt to create elegant underline as a span
 * <ul>
 * <li>in a `TextView` span is rendered, but `draw` method is invoked constantly which put pressure on CPU and memory
 * <li>in an `EditText` only the first line draws this underline span (seems to be a weird
 * issue between LineBackgroundSpan and EditText). Also, in `EditText` `draw` method is invoked
 * constantly (for each drawing of the blinking cursor)
 * <li>cannot reliably receive proper text, for example if underline is applied to a text range which has
 * different typefaces applied to different words (underline cannot know that, which applied to which)
 * </ul>
 */
// will apply other spans that 100% contain this one, so for example if
// an underline that inside some other spans (different typeface), they won't be applied and thus
// underline would be incorrect
// do not use in editor, due to some obscure thing, LineBackgroundSpan would be applied to the first line only
// also, in editor this span would be redrawn with each blink of the cursor
@RequiresApi(Build.VERSION_CODES.KITKAT)
class ElegantUnderlineSpan implements LineBackgroundSpan {

    private static final float DEFAULT_UNDERLINE_HEIGHT_DIP = 0.8F;
    private static final float DEFAULT_UNDERLINE_CLEAR_GAP_DIP = 5.5F;

    @NonNull
    public static ElegantUnderlineSpan create() {
        return new ElegantUnderlineSpan(0, 0);
    }

    @NonNull
    public static ElegantUnderlineSpan create(@Px int underlineHeight) {
        return new ElegantUnderlineSpan(underlineHeight, 0);
    }

    @NonNull
    public static ElegantUnderlineSpan create(@Px int underlineHeight, @Px int underlineClearGap) {
        return new ElegantUnderlineSpan(underlineHeight, underlineClearGap);
    }

    // TODO: underline color?
    private final int underlineHeight;
    private final int underlineClearGap;

    private final Path underline = new Path();
    private final Path outline = new Path();
    private final Paint stroke = new Paint();
    private final Path strokedOutline = new Path();

    private final CharCache charCache = new CharCache();

    private final TextPaint tempTextPaint = new TextPaint();

    protected ElegantUnderlineSpan(@Px int underlineHeight, @Px int underlineClearGap) {
        this.underlineHeight = underlineHeight;
        this.underlineClearGap = underlineClearGap;
        stroke.setStyle(Paint.Style.FILL_AND_STROKE);
        stroke.setStrokeCap(Paint.Cap.BUTT);
    }

    // is it possible that LineBackgroundSpan is not receiving proper spans? like typeface?
    //  it complicates things (like the need to have own copy of paint)

    // is it possible that LineBackgroundSpan is called constantly even in a TextView?

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

//        Debug.trace();

        final Spanned spanned = (Spanned) text;
        final TextView textView = TextViewSpan.textViewOf(spanned);

        if (textView == null) {
            // TextView is required
            Log.e("EU", "no text view");
            return;
        }

        final Layout layout;
        {
            // check if there is dedicated layout, if not, use from textView
            //  (think tableRowSpan that uses own Layout)
            final Layout layoutFromSpan = TextLayoutSpan.layoutOf(spanned);
            if (layoutFromSpan != null) {
                layout = layoutFromSpan;
            } else {
                layout = textView.getLayout();
            }
        }

        if (layout == null) {
            // we could call `p.setUnderlineText(true)` here a fallback,
            //  but this would make __all__ text in a TextView underlined, which is not
            //  what we want
            Log.e("EU", "no layout");
            return;
        }

        tempTextPaint.set((TextPaint) p);

        // we must use _selfStart_ because underline can start **not** at the beginning of a line.
        // as we are using LineBackground `start` would indicate the start position of the line
        //  and not start of the span (self). The same goes for selfEnd (ended before line)
        final int selfStart = spanned.getSpanStart(this);
        final int selfEnd = spanned.getSpanEnd(this);

        final int s = max(selfStart, start);

        // all lines should use (end - 1) to receive proper line end coordinate X,
        //  unless it is last line in _layout_
        final boolean isLastLine = lnum == (layout.getLineCount() - 1);
        final int e = min(selfEnd, end - (isLastLine ? 0 : 1));

        if (true) {
            Log.e("EU", String.format("lnum: %s, hash: %s, text: '%s'",
                    lnum, text.subSequence(s, e).hashCode(), text.subSequence(s, e)));
        }

        final int leading;
        final int trailing;
        {
            final int l = (int) (layout.getPrimaryHorizontal(s) + .5F);
            final int r = (int) (layout.getPrimaryHorizontal(e) + .5F);
            leading = min(l, r);
            trailing = max(l, r);
        }

        underline.rewind();

        // middle between baseline and descent
        final int diff = (int) (p.descent() / 2F + .5F);

        underline.addRect(
                leading, baseline + diff,
                trailing, baseline + diff + underlineHeight(textView),
                Path.Direction.CW
        );

        outline.rewind();

        final int charsLength = e - s;
        final char[] chars = charCache.chars(charsLength);
        TextUtils.getChars(spanned, s, e, chars, 0);

        if (true) {
            final MetricAffectingSpan[] metricAffectingSpans = spanned.getSpans(s, e, MetricAffectingSpan.class);
//            Log.e("EU", Arrays.toString(metricAffectingSpans));
            for (MetricAffectingSpan span : metricAffectingSpans) {
                span.updateMeasureState(tempTextPaint);
            }
        }

        // todo: styleSpan
        // todo all other spans (maybe UpdateMeasureSpans?)
        tempTextPaint.getTextPath(
                chars,
                0, charsLength,
                leading, baseline,
                outline
        );

        outline.op(underline, Path.Op.INTERSECT);

        strokedOutline.rewind();
        stroke.setStrokeWidth(underlineClearGap(textView));
        stroke.getFillPath(outline, strokedOutline);

        underline.op(strokedOutline, Path.Op.DIFFERENCE);

        c.drawPath(underline, p);
    }

    private int underlineHeight(@NonNull TextView textView) {
        if (underlineHeight > 0) {
            return underlineHeight;
        }
        return (int) (DEFAULT_UNDERLINE_HEIGHT_DIP * textView.getResources().getDisplayMetrics().density + 0.5F);
    }

    private int underlineClearGap(@NonNull TextView textView) {
        if (underlineClearGap > 0) {
            return underlineClearGap;
        }
        return (int) (DEFAULT_UNDERLINE_CLEAR_GAP_DIP * textView.getResources().getDisplayMetrics().density + 0.5F);
    }

    // primitive cache that grows internal array (never shrinks, nor clear buffer)
    // TODO: but... each span has own instance, so not much of the memory saving
    private static class CharCache {

        @NonNull
        char[] chars(int ofLength) {
            final char[] out;
            if (chars == null || chars.length < ofLength) {
                out = chars = new char[ofLength];
            } else {
                out = chars;
            }
            return out;
        }

        private char[] chars;
    }
}

