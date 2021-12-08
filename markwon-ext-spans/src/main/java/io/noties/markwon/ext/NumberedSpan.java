package io.noties.markwon.span.ext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

import com.campuswire.android.messenger.model.THEMETYPE;

class NumberedSpan implements LeadingMarginSpan {
    private final int NUMBER_GAP = 14;
    private final int mIndex;
    private final String mTheme;
    private final Paint paint = ObjectsPool.paint();

    NumberedSpan(int index, String  theme) {
        mIndex = index;
        mTheme = theme;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        final String numText = mIndex + ".";
        final Rect textBounds = new Rect();
        paint.getTextBounds(numText, 0, numText.length(), textBounds);
        return textBounds.width() + NUMBER_GAP;
    }

    @Override
    public void drawLeadingMargin(Canvas canvas, Paint paint, int x, int dir, int top, int baseline,
                                  int bottom, CharSequence text, int start, int end, boolean first,
                                  Layout layout) {
        // add number to first line
        if (((Spanned)text).getSpanStart(this) == start) {
            // save previous paint values
            final Paint.Style prevStyle = paint.getStyle();
            final int prevColor = paint.getColor();

            // draw number
            paint.setStyle(Paint.Style.FILL);
            if(mTheme.equalsIgnoreCase(THEMETYPE.DARK.toString())){
                paint.setColor(Color.WHITE);
            }else{
                paint.setColor(Color.BLACK);
            }
            final String numText = mIndex + ".";
            final Rect textBounds = new Rect();
            paint.getTextBounds(numText, 0, numText.length(), textBounds);
            final float yVal = (top + bottom + textBounds.height()) / 2f;
            canvas.drawText(numText, 0, numText.length(), x, yVal, paint);

            // reset modified paint values
            paint.setStyle(prevStyle);
            paint.setColor(prevColor);
        }
    }
}
