package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineHeightSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;

import ru.noties.debug.Debug;

// we will use Replacement span because code blocks cannot contain other markdown
// so we will render the string (not a charSequence with possible metric affecting spans)
public class CodeSpan extends ReplacementSpan/* implements LeadingMarginSpan*/ {

    private final boolean multiline;
    private final int start;
    private final int end;

    public CodeSpan(boolean multiline, int start, int end) {
        this.multiline = multiline;
        this.start = start;
        this.end = end;
    }

    @Override
    public int getSize(
            @NonNull Paint paint,
            CharSequence text,
            @IntRange(from = 0) int start,
            @IntRange(from = 0) int end,
            @Nullable Paint.FontMetricsInt fm
    ) {

        final CharSequence cs = text.subSequence(start, end);
        final int width = 32 + (int) (paint.measureText(cs, 0, cs.length()) + .5F);

//        final StaticLayout layout = new StaticLayout(cs, new TextPaint(paint), 10000, Layout.Alignment.ALIGN_NORMAL, 1.F, .0F, false);
//        final float width = layout.getLineWidth(0);
//        final int out = 32 + (int) (width + .5F);

//        Debug.i("text: %s, width: %s", cs, width);

        if (fm != null) {
            // we add a padding top & bottom
            Debug.i("a: %s, d: %s, t: %s, b: %s", fm.ascent, fm.descent, fm.top, fm.bottom);
            final float ratio = .62F; // golden ratio
            fm.ascent = fm.ascent - 8;
            fm.descent = (int) (-fm.ascent * ratio);
            fm.top = fm.ascent;
            fm.bottom = fm.descent;
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
            @NonNull Paint paint
    ) {

        Debug.i("text: %s, x: %s, top: %s, y: %s, bottom: %s", text.subSequence(start, end), x, top, y, bottom);

        final CharSequence cs = text.subSequence(start, end);

        final int width = 32 + (int) (paint.measureText(cs, 0, cs.length()) + .5F);

        final int left = (int) (x + .5F);
        final int right = multiline
                ? canvas.getWidth()
                : left + width;

        final Rect rect = new Rect(
                left,
                top,
                right,
                bottom
        );

        final Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(0x80ff0000);
        canvas.drawRect(rect, p);

        // y center position
        final int b = bottom - ((bottom - top) / 2) - (int) ((paint.descent() + paint.ascent()) / 2);
        p.setColor(0xFF000000);
        canvas.drawText(cs, 0, cs.length(), x + 16, b, paint);
    }


//    @Override
//    public int getLeadingMargin(boolean first) {
//        return 1;
//    }
//
//    @Override
//    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
////        Debug.i("x: %d, top: %d, bottom: %d", x, top, bottom);
//
////        Debug.i("this: [%d, %d], came: [%d, %d]", this.start, this.end, start, end);
//        Debug.i("x: %d, canvas: [%d-%d], text: %s", x, c.getWidth(), c.getHeight(), (text.subSequence(start, end)));
//
//        // the thing is... if we do not draw, then text won't be drawn also
//        final Rect rect = new Rect();
//
//        final Paint paint = new Paint();
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(0xffcccccc);
//
//        rect.set(x, top, c.getWidth(), bottom);
//        c.drawRect(rect, paint);
//
//        if (this.start == start) {
//            this.top = top;
//
////            final int save = c.save();
////            try {
////                c.drawColor(0x00ffffff);
////            } finally {
////                c.restoreToCount(save);
////            }
//
////            c.drawColor(0x00ffffff);
//        }
//
//        if (this.end == end) {
//            // draw borders
//            final Rect r = new Rect(x + 1, this.top, c.getWidth() - x, bottom);
//            final Paint pa = new Paint();
//            pa.setStyle(Paint.Style.STROKE);
//            pa.setColor(0xff999999);
//            c.drawRect(r, pa);
//        }
////        rect.inset((int) paint.getStrokeWidth(), (int) paint.getStrokeWidth());
////        paint.setStyle(Paint.Style.STROKE);
////        paint.setColor(0xff333333);
////        c.drawRect(rect, paint);
//    }

//    @Override
//    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
////        int ht = mDrawable.getIntrinsicHeight();
////
////        int need = ht - (v + fm.descent - fm.ascent - istartv);
////        if (need > 0)
////            fm.descent += need;
////
////        need = ht - (v + fm.bottom - fm.top - istartv);
////        if (need > 0)
////            fm.bottom += need;
////
//
////        final int lineOffset = v - spanstartv;
////        final int desired = 128;
////        final int currentLineHeight = -fm.ascent + fm.descent;
////        final float ratio = (float) desired / currentLineHeight;
////
////        Debug.i("fm, came: %s", fm);
////        Debug.i("lineOffset: %d, current: %d, ratio: %s", lineOffset, currentLineHeight, ratio);
////
////        fm.ascent = (int) (ratio * fm.ascent + .5F);
////        fm.descent = (int) (ratio * fm.descent + .5F);
////
////        Debug.i("fm, out: %s", fm);
//
////        Debug.i("top: %d, bottom: %d, ascent: %d, descent: %d", fm.top, fm.bottom, fm.ascent, fm.descent);
////        Debug.i("lineHeight: %d, v: %d, spanstartv: %d", lineOffset, v, spanstartv);
////
////        final int h = 128;
////        final int descentNeed = h - (v + fm.descent - fm.ascent - spanstartv);
////        if (descentNeed > 0) {
////            fm.ascent -= descentNeed / 2;
////            fm.descent += descentNeed / 2;
////        }
////        final int bottomNeed = h - (v + fm.bottom - fm.top - spanstartv);
////        if (bottomNeed > 0) {
////            fm.top -= bottomNeed;
////            fm.bottom += bottomNeed;
////        }
////
////        Debug.i("out, ascent: %d, descent: %d, bottom: %d", fm.ascent, fm.descent, fm.bottom);
//    }
}
