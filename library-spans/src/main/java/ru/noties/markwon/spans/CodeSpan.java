package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.MetricAffectingSpan;

public class CodeSpan extends MetricAffectingSpan implements LeadingMarginSpan {

    // the thing is.. we cannot use replacementSpan, because it won't let us create multiline code..
    // and we want new lines when we do not fit the width
    // plus it complicates the copying

    // replacement span is great because we can have additional paddings & can actually get a hold
    // of Canvas to draw background, but it implies a lot of manual text handling

    // also, we can reuse Rect instance as long as we apply our dimensions in each draw call

    @SuppressWarnings("WeakerAccess")
    public static class Config {

        public static Builder builder() {
            return new Builder();
        }

        final int textColor; // by default the same as main text
        final int backgroundColor; // by default textColor with 0.1 alpha
        final int multilineMargin; // by default 0
        final int textSize; // by default the same as main text
        final Typeface typeface; // by default Typeface.MONOSPACE

        private Config(Builder builder) {
            this.textColor = builder.textColor;
            this.backgroundColor = builder.backgroundColor;
            this.multilineMargin = builder.multilineMargin;
            this.textSize = builder.textSize;
            this.typeface = builder.typeface;
        }

        public static class Builder {

            int textColor;
            int backgroundColor;
            int multilineMargin;
            int textSize;
            Typeface typeface;

            public Builder setTextColor(@ColorInt int textColor) {
                this.textColor = textColor;
                return this;
            }

            public Builder setBackgroundColor(@ColorInt int backgroundColor) {
                this.backgroundColor = backgroundColor;
                return this;
            }

            public Builder setMultilineMargin(int multilineMargin) {
                this.multilineMargin = multilineMargin;
                return this;
            }

            public Builder setTextSize(@IntRange(from = 0) int textSize) {
                this.textSize = textSize;
                return this;
            }

            public Builder setTypeface(@NonNull Typeface typeface) {
                this.typeface = typeface;
                return this;
            }

            public Config build() {
                if (typeface == null) {
                    typeface = Typeface.MONOSPACE;
                }
                return new Config(this);
            }
        }
    }

    private final Config config;
    private final Rect rect = new Rect();
    private final Paint paint = new Paint();

    private final boolean multiline;

    public CodeSpan(@NonNull Config config, boolean multiline) {
        this.config = config;
        this.multiline = multiline;

        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(config.typeface);
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        apply(p);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        apply(ds);
        if (!multiline) {
            final int color;
            if (config.backgroundColor == 0) {
                color = applyAlpha(ds.getColor(), 25);
            } else {
                color = config.backgroundColor;
            }
            ds.bgColor = color;
        }
    }

    private void apply(TextPaint p) {
        p.setTypeface(config.typeface);
        if (config.textSize > 0) {
            p.setTextSize(config.textSize);
        }
        if (config.textColor != 0) {
            p.setColor(config.textColor);
        }
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return multiline ? config.multilineMargin : 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        if (multiline) {

            final int color;
            if (config.backgroundColor == 0) {
                color = applyAlpha(p.getColor(), 25);
            } else {
                color = config.backgroundColor;
            }
            paint.setColor(color);

            rect.set(x, top, c.getWidth(), bottom);

            c.drawRect(rect, paint);
        }

//        paint.setTextSize(p.getTextSize());
//
//        final int left = (int) (x + .5F);
//
//        final int right;
//        if (multiline) {
//            right = c.getWidth();
//        } else {
//            final int width = (config.paddingHorizontal * 2) + (int) (paint.measureText(text, start, end) + .5F);
//            right = left + width;
//        }
//
//        rect.set(left, top, right, bottom);
//
//        // okay, draw background first
//        drawBackground(c);

        // then, if any, draw borders
//        drawBorders(c, this.start == start, this.end == end);

//        final int color;
//        if (config.textColor == 0) {
//            color = p.getColor();
//        } else {
//            color = config.textColor;
//        }
//        paint.setColor(color);
//
//        // draw text
//        // y center position
//        final int b = bottom - ((bottom - top) / 2) - (int) ((paint.descent() + paint.ascent()) / 2);
//        canvas.drawText(text, start, end, x + config.paddingHorizontal, b, paint);
    }

    private static int applyAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }


//    @Override
//    public int getSize(
//            @NonNull Paint p,
//            CharSequence text,
//            @IntRange(from = 0) int start,
//            @IntRange(from = 0) int end,
//            @Nullable Paint.FontMetricsInt fm
//    ) {
//
//        paint.setTextSize(p.getTextSize());
//
//        final int width = (config.paddingHorizontal * 2) + (int) (paint.measureText(text, start, end) + .5F);
//
//        if (fm != null) {
//            // we add a padding top & bottom
//            final float ratio = .62F; // golden ratio, there is no much point of moving this to config... it seems a bit `specific`...
//            fm.ascent = fm.ascent - (config.paddingVertical);
//            fm.descent = (int) (-fm.ascent * ratio);
//            fm.top = fm.ascent;
//            fm.bottom = fm.descent;
//        }
//
//        return width;
//    }

//    @Override
//    public void draw(
//            @NonNull Canvas canvas,
//            CharSequence text,
//            @IntRange(from = 0) int start,
//            @IntRange(from = 0) int end,
//            float x,
//            int top,
//            int y,
//            int bottom,
//            @NonNull Paint p
//    ) {
//
//        paint.setTextSize(p.getTextSize());
//
//        final int left = (int) (x + .5F);
//
//        final int right;
//        if (multiline) {
//            right = canvas.getWidth();
//        } else {
//            final int width = (config.paddingHorizontal * 2) + (int) (paint.measureText(text, start, end) + .5F);
//            right = left + width;
//        }
//
//        rect.set(left, top, right, bottom);
//
//        // okay, draw background first
//        drawBackground(canvas);
//
//        // then, if any, draw borders
//        drawBorders(canvas, this.start == start, this.end == end);
//
//        final int color;
//        if (config.textColor == 0) {
//            color = p.getColor();
//        } else {
//            color = config.textColor;
//        }
//        paint.setColor(color);
//
//        // draw text
//        // y center position
//        final int b = bottom - ((bottom - top) / 2) - (int) ((paint.descent() + paint.ascent()) / 2);
//        canvas.drawText(text, start, end, x + config.paddingHorizontal, b, paint);
//    }

//    private void drawBackground(Canvas canvas) {
//        final int color = config.backgroundColor;
//        if (color != 0) {
//            paint.setColor(color);
//            canvas.drawRect(rect, paint);
//        }
//    }
//
//    private void drawBorders(Canvas canvas, boolean top, boolean bottom) {
//
//        final int color = config.borderColor;
//        final int width = config.borderWidth;
//        if (color == 0
//                || width == 0) {
//            return;
//        }
//
//        paint.setColor(color);
//
//        // left and right are always drawn
//
//        // LEFT
//        borderRect.set(rect.left, rect.top, rect.left + width, rect.bottom);
//        canvas.drawRect(borderRect, paint);
//
//        // RIGHT
//        borderRect.set(rect.right - width, rect.top, rect.right, rect.bottom);
//        canvas.drawRect(borderRect, paint);
//
//        // TOP
//        if (top) {
//            borderRect.set(rect.left, rect.top, rect.right, rect.top + width);
//            canvas.drawRect(borderRect, paint);
//        }
//
//        // BOTTOM
//        if (bottom) {
//            borderRect.set(rect.left, rect.bottom - width, rect.right, rect.bottom);
//            canvas.drawRect(borderRect, paint);
//        }
//    }
}
