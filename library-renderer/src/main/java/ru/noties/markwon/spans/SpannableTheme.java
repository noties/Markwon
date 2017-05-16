package ru.noties.markwon.spans;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.AttrRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.TypedValue;
import android.widget.TextView;

@SuppressWarnings("WeakerAccess")
public class SpannableTheme {

    // this method should be used if TextView is known beforehand
    // it will correctly measure the `space` char and set it as `codeMultilineMargin`
    // otherwise this value must be set explicitly (
    public static SpannableTheme create(@NonNull TextView textView) {
        return builderWithDefaults(textView.getContext())
                .codeMultilineMargin((int) (textView.getPaint().measureText("\u00a0") + .5F))
                .build();
    }

    // this create default theme (except for `codeMultilineMargin` property)
    public static SpannableTheme create(@NonNull Context context) {
        return builderWithDefaults(context).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@NonNull SpannableTheme copyFrom) {
        return new Builder(copyFrom);
    }

    public static Builder builderWithDefaults(@NonNull Context context) {
        final Px px = new Px(context);
        return new Builder()
                .linkColor(resolve(context, android.R.attr.textColorLink))
                .codeMultilineMargin(px.px(8))
                .blockMargin(px.px(24))
                .bulletListItemStrokeWidth(px.px(1))
                .headingBreakHeight(px.px(1))
                .thematicBreakHeight(px.px(2));
    }

    private static int resolve(Context context, @AttrRes int attr) {
        final TypedValue typedValue = new TypedValue();
        final int attrs[] = new int[]{attr};
        final TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, attrs);
        try {
            return typedArray.getColor(0, 0);
        } finally {
            typedArray.recycle();
        }
    }

    protected static final int BLOCK_QUOTE_DEF_COLOR_ALPHA = 50;

    protected static final int CODE_DEF_BACKGROUND_COLOR_ALPHA = 25;
    protected static final float CODE_DEF_TEXT_SIZE_RATIO = .87F;

    protected static final int HEADING_DEF_BREAK_COLOR_ALPHA = 75;

    // taken from html spec (most browsers render headings like that)
    // is not exposed via protected modifier in order to disallow modification
    private static final float[] HEADING_SIZES = {
            2.F, 1.5F, 1.17F, 1.F, .83F, .67F,
    };

    protected static final float SCRIPT_DEF_TEXT_SIZE_RATIO = .75F;

    protected static final int THEMATIC_BREAK_DEF_ALPHA = 75;

    protected final int linkColor;

    // used in quote, lists
    protected final int blockMargin;

    // by default it's 1/4th of `blockMargin`
    protected final int blockQuoteWidth;

    // by default it's text color with `BLOCK_QUOTE_DEF_COLOR_ALPHA` applied alpha
    protected final int blockQuoteColor;

    // by default uses text color (applied for un-ordered lists & ordered (bullets & numbers)
    protected final int listItemColor;

    // by default the stroke color of a paint object
    protected final int bulletListItemStrokeWidth;

    // width of bullet, by default min(blockMargin, height) / 2
    protected final int bulletWidth;

    // by default - main text color
    protected final int codeTextColor;

    // by default 0.1 alpha of textColor/codeTextColor
    protected final int codeBackgroundColor;

    // by default `width` of a space char... it's fun and games, but span doesn't have access to paint in `getLeadingMargin`
    // so, we need to set this value explicitly (think of an utility method, that takes TextView/TextPaint and measures space char)
    protected final int codeMultilineMargin;

    // by default Typeface.MONOSPACE
    protected final Typeface codeTypeface;

    // by default a bit (how much?!) smaller than normal text
    // applied ONLY if default typeface was used, otherwise, not applied
    protected final int codeTextSize;

    // by default paint.getStrokeWidth
    protected final int headingBreakHeight;

    // by default, text color with `HEADING_DEF_BREAK_COLOR_ALPHA` applied alpha
    protected final int headingBreakColor;

    // by default `SCRIPT_DEF_TEXT_SIZE_RATIO`
    protected final float scriptTextSizeRatio;

    // by default textColor with `THEMATIC_BREAK_DEF_ALPHA` applied alpha
    protected final int thematicBreakColor;

    // by default paint.strokeWidth
    protected final int thematicBreakHeight;

    protected SpannableTheme(@NonNull Builder builder) {
        this.linkColor = builder.linkColor;
        this.blockMargin = builder.blockMargin;
        this.blockQuoteWidth = builder.blockQuoteWidth;
        this.blockQuoteColor = builder.blockQuoteColor;
        this.listItemColor = builder.listItemColor;
        this.bulletListItemStrokeWidth = builder.bulletListItemStrokeWidth;
        this.bulletWidth = builder.bulletWidth;
        this.codeTextColor = builder.codeTextColor;
        this.codeBackgroundColor = builder.codeBackgroundColor;
        this.codeMultilineMargin = builder.codeMultilineMargin;
        this.codeTypeface = builder.codeTypeface;
        this.codeTextSize = builder.codeTextSize;
        this.headingBreakHeight = builder.headingBreakHeight;
        this.headingBreakColor = builder.headingBreakColor;
        this.scriptTextSizeRatio = builder.scriptTextSizeRatio;
        this.thematicBreakColor = builder.thematicBreakColor;
        this.thematicBreakHeight = builder.thematicBreakHeight;
    }


    public void applyLinkStyle(@NonNull Paint paint) {
        paint.setUnderlineText(true);
        if (linkColor != 0) {
            // by default we will be using text color
            paint.setColor(linkColor);
        }
    }

    public void applyBlockQuoteStyle(@NonNull Paint paint) {
        final int color;
        if (blockQuoteColor == 0) {
            color = ColorUtils.applyAlpha(paint.getColor(), BLOCK_QUOTE_DEF_COLOR_ALPHA);
        } else {
            color = blockQuoteColor;
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
    }

    public int getBlockMargin() {
        return blockMargin;
    }

    public int getBlockQuoteWidth() {
        final int out;
        if (blockQuoteWidth == 0) {
            out = (int) (blockMargin * .25F + .5F);
        } else {
            out = blockQuoteWidth;
        }
        return out;
    }

    public void applyListItemStyle(@NonNull Paint paint) {

        final int color;
        if (listItemColor != 0) {
            color = listItemColor;
        } else {
            color = paint.getColor();
        }
        paint.setColor(color);

        if (bulletListItemStrokeWidth != 0) {
            paint.setStrokeWidth(bulletListItemStrokeWidth);
        }
    }

    public int getBulletWidth(int height) {

        final int min = Math.min(blockMargin, height) / 2;

        final int width;
        if (bulletWidth == 0
                || bulletWidth > min) {
            width = min;
        } else {
            width = bulletWidth;
        }

        return width;
    }

    public void applyCodeTextStyle(@NonNull Paint paint) {

        if (codeTextColor != 0) {
            paint.setColor(codeTextColor);
        }

        // custom typeface was set
        if (codeTypeface != null) {
            paint.setTypeface(codeTypeface);
            if (codeTextSize != 0) {
                paint.setTextSize(codeTextSize);
            }
        } else {
            paint.setTypeface(Typeface.MONOSPACE);
            final float textSize;
            if (codeTextSize != 0) {
                textSize = codeTextSize;
            } else {
                textSize = paint.getTextSize() * CODE_DEF_TEXT_SIZE_RATIO;
            }
            paint.setTextSize(textSize);
        }
    }

    public int getCodeMultilineMargin() {
        return codeMultilineMargin;
    }

    public int getCodeBackgroundColor(@NonNull Paint paint) {
        final int color;
        if (codeBackgroundColor != 0) {
            color = codeBackgroundColor;
        } else {
            color = ColorUtils.applyAlpha(paint.getColor(), CODE_DEF_BACKGROUND_COLOR_ALPHA);
        }
        return color;
    }

    public void applyHeadingTextStyle(@NonNull Paint paint, @IntRange(from = 1, to = 6) int level) {
        paint.setFakeBoldText(true);
        paint.setTextSize(paint.getTextSize() * HEADING_SIZES[level - 1]);
    }

    public void applyHeadingBreakStyle(@NonNull Paint paint) {
        final int color;
        if (headingBreakColor != 0) {
            color = headingBreakColor;
        } else {
            color = ColorUtils.applyAlpha(paint.getColor(), HEADING_DEF_BREAK_COLOR_ALPHA);
        }
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        if (headingBreakHeight != 0) {
            //noinspection SuspiciousNameCombination
            paint.setStrokeWidth(headingBreakHeight);
        }
    }

    public void applySuperScriptStyle(@NonNull TextPaint paint) {
        final float ratio;
        if (Float.compare(scriptTextSizeRatio, .0F) == 0) {
            ratio = SCRIPT_DEF_TEXT_SIZE_RATIO;
        } else {
            ratio = scriptTextSizeRatio;
        }
        paint.setTextSize(paint.getTextSize() * ratio);
        paint.baselineShift += (int) (paint.ascent() / 2);
    }

    public void applySubScriptStyle(@NonNull TextPaint paint) {
        final float ratio;
        if (Float.compare(scriptTextSizeRatio, .0F) == 0) {
            ratio = SCRIPT_DEF_TEXT_SIZE_RATIO;
        } else {
            ratio = scriptTextSizeRatio;
        }
        paint.setTextSize(paint.getTextSize() * ratio);
        paint.baselineShift -= (int) (paint.ascent() / 2);
    }

    public void applyThematicBreakStyle(@NonNull Paint paint) {
        final int color;
        if (thematicBreakColor != 0) {
            color = thematicBreakColor;
        } else {
            color = ColorUtils.applyAlpha(paint.getColor(), THEMATIC_BREAK_DEF_ALPHA);
        }
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        if (thematicBreakHeight != 0) {
            //noinspection SuspiciousNameCombination
            paint.setStrokeWidth(thematicBreakHeight);
        }
    }

    public static class Builder {

        private int linkColor;
        private int blockMargin;
        private int blockQuoteWidth;
        private int blockQuoteColor;
        private int listItemColor;
        private int bulletListItemStrokeWidth;
        private int bulletWidth;
        private int codeTextColor;
        private int codeBackgroundColor;
        private int codeMultilineMargin;
        private Typeface codeTypeface;
        private int codeTextSize;
        private int headingBreakHeight;
        private int headingBreakColor;
        private float scriptTextSizeRatio;
        private int thematicBreakColor;
        private int thematicBreakHeight;

        Builder() {

        }

        Builder(@NonNull SpannableTheme theme) {

            this.linkColor = theme.linkColor;
            this.blockMargin = theme.blockMargin;
            this.blockQuoteWidth = theme.blockQuoteWidth;
            this.blockQuoteColor = theme.blockQuoteColor;
            this.listItemColor = theme.listItemColor;
            this.bulletListItemStrokeWidth = theme.bulletListItemStrokeWidth;
            this.bulletWidth = theme.bulletWidth;
            this.codeTextColor = theme.codeTextColor;
            this.codeBackgroundColor = theme.codeBackgroundColor;
            this.codeMultilineMargin = theme.codeMultilineMargin;
            this.codeTypeface = theme.codeTypeface;
            this.codeTextSize = theme.codeTextSize;
            this.headingBreakHeight = theme.headingBreakHeight;
            this.headingBreakColor = theme.headingBreakColor;
            this.scriptTextSizeRatio = theme.scriptTextSizeRatio;
            this.thematicBreakColor = theme.thematicBreakColor;
            this.thematicBreakHeight = theme.thematicBreakHeight;
        }

        public Builder linkColor(int linkColor) {
            this.linkColor = linkColor;
            return this;
        }

        public Builder blockMargin(int blockMargin) {
            this.blockMargin = blockMargin;
            return this;
        }

        public Builder blockQuoteWidth(int blockQuoteWidth) {
            this.blockQuoteWidth = blockQuoteWidth;
            return this;
        }

        public Builder blockQuoteColor(int blockQuoteColor) {
            this.blockQuoteColor = blockQuoteColor;
            return this;
        }

        public Builder listItemColor(int listItemColor) {
            this.listItemColor = listItemColor;
            return this;
        }

        public Builder bulletListItemStrokeWidth(int bulletListItemStrokeWidth) {
            this.bulletListItemStrokeWidth = bulletListItemStrokeWidth;
            return this;
        }

        public Builder bulletWidth(int bulletWidth) {
            this.bulletWidth = bulletWidth;
            return this;
        }

        public Builder codeTextColor(int codeTextColor) {
            this.codeTextColor = codeTextColor;
            return this;
        }

        public Builder codeBackgroundColor(int codeBackgroundColor) {
            this.codeBackgroundColor = codeBackgroundColor;
            return this;
        }

        public Builder codeMultilineMargin(int codeMultilineMargin) {
            this.codeMultilineMargin = codeMultilineMargin;
            return this;
        }

        public Builder codeTypeface(Typeface codeTypeface) {
            this.codeTypeface = codeTypeface;
            return this;
        }

        public Builder codeTextSize(int codeTextSize) {
            this.codeTextSize = codeTextSize;
            return this;
        }

        public Builder headingBreakHeight(int headingBreakHeight) {
            this.headingBreakHeight = headingBreakHeight;
            return this;
        }

        public Builder headingBreakColor(int headingBreakColor) {
            this.headingBreakColor = headingBreakColor;
            return this;
        }

        public Builder scriptTextSizeRatio(float scriptTextSizeRatio) {
            this.scriptTextSizeRatio = scriptTextSizeRatio;
            return this;
        }

        public Builder thematicBreakColor(int thematicBreakColor) {
            this.thematicBreakColor = thematicBreakColor;
            return this;
        }

        public Builder thematicBreakHeight(int thematicBreakHeight) {
            this.thematicBreakHeight = thematicBreakHeight;
            return this;
        }

        public SpannableTheme build() {
            return new SpannableTheme(this);
        }
    }

    private static class Px {
        private final float density;

        Px(@NonNull Context context) {
            this.density = context.getResources().getDisplayMetrics().density;
        }

        int px(int dp) {
            return (int) (dp * density + .5F);
        }
    }
}
