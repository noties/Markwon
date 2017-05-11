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

    private static final int DEF_COLOR_ALPHA = 25;

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
    private final Rect rect = ObjectsPool.rect();
    private final Paint paint = ObjectsPool.paint();

    private final boolean multiline;

    public CodeSpan(@NonNull Config config, boolean multiline) {
        this.config = config;
        this.multiline = multiline;
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
                color = ColorUtils.applyAlpha(ds.getColor(), DEF_COLOR_ALPHA);
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
                color = ColorUtils.applyAlpha(p.getColor(), DEF_COLOR_ALPHA);
            } else {
                color = config.backgroundColor;
            }
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);

            rect.set(x, top, c.getWidth(), bottom);

            c.drawRect(rect, paint);
        }
    }
}
