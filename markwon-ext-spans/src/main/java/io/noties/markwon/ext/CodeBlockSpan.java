package io.noties.markwon.span.ext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineHeightSpan;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

import com.campuswire.android.messenger.model.THEMETYPE;

import io.noties.markwon.core.MarkwonTheme;


public class CodeBlockSpan extends MetricAffectingSpan implements LeadingMarginSpan, LineHeightSpan {
    protected static final float CODE_DEF_TEXT_SIZE_RATIO = 1.0F;
    protected static final int DEFAULT_LEADING_MARGIN = 20;
    protected int BACKGROUND_COLOR;
    protected int TEXT_COLOR;

    protected static final int CORNER_RADIUS = 15;
    private final int VERTICAL_SPACING = 40;

    private final Rect rect = ObjectsPool.rect();
    private final Paint paint = ObjectsPool.paint();

    public CodeBlockSpan(String theme) {
        if(theme.equalsIgnoreCase(THEMETYPE.DARK.toString())){
            BACKGROUND_COLOR = Color.argb(255, 25, 26, 27);
            TEXT_COLOR = Color.WHITE;
        }else{
            BACKGROUND_COLOR = Color.argb(255, 246, 246, 246);
            TEXT_COLOR = Color.BLACK;
        }
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        apply(p);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        apply(ds);
    }

    private void apply(TextPaint p) {
        final int textColor = TEXT_COLOR;

        if (textColor != 0) {
            paint.setColor(textColor);
        }

        paint.setTypeface(Typeface.MONOSPACE);

        final int textSize = 0;

        if (textSize > 0) {
            paint.setTextSize(textSize);
        } else {
            // calculate default value
            paint.setTextSize(paint.getTextSize() * CODE_DEF_TEXT_SIZE_RATIO);
        }
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return DEFAULT_LEADING_MARGIN;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(BACKGROUND_COLOR);

        final int left;
        final int right;
        if (dir > 0) {
            left = x;
            right = c.getWidth();
        } else {
            left = x - c.getWidth();
            right = x;
        }

        rect.set(left, top, right, bottom);

        final Spanned txt = (Spanned)text;
        final int spanEnd = txt.getSpanEnd(this);
        final int spanStart = txt.getSpanStart(this);

        // draw rounded corner background
        if (start == spanStart) {
            c.drawRoundRect(new RectF(rect), CORNER_RADIUS, CORNER_RADIUS, paint);
            c.drawRect(new Rect(left, top + CORNER_RADIUS, right, bottom), paint);
        }
        else if (Math.abs(spanEnd - end) <= 1) {
            c.drawRoundRect(new RectF(rect), CORNER_RADIUS, CORNER_RADIUS, paint);
            c.drawRect(new Rect(left, top, right, bottom - CORNER_RADIUS), paint);
        }
        else {
            c.drawRect(rect, paint);
        }
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
