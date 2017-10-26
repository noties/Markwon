package ru.noties.markwon.spans;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.TypedValue;

@SuppressWarnings("WeakerAccess")
public class SpannableTheme {

    /**
     * Factory method to obtain an instance of {@link SpannableTheme} with all values as defaults
     *
     * @param context Context in order to resolve defaults
     * @return {@link SpannableTheme} instance
     * @see #builderWithDefaults(Context)
     * @since 1.0.0
     */
    @NonNull
    public static SpannableTheme create(@NonNull Context context) {
        return builderWithDefaults(context).build();
    }

    /**
     * Factory method to obtain an instance of {@link Builder}. Please note, that no default
     * values are set. This might be useful if you require a lot of special styling that differs
     * a lot with default one
     *
     * @return {@link Builder instance}
     * @see #builderWithDefaults(Context)
     * @see #builder(SpannableTheme)
     * @since 1.0.0
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Factory method to create a {@link Builder} instance and initialize it with values
     * from supplied {@link SpannableTheme}
     *
     * @param copyFrom {@link SpannableTheme} to copy values from
     * @return {@link Builder} instance
     * @see #builderWithDefaults(Context)
     * @since 1.0.0
     */
    @NonNull
    public static Builder builder(@NonNull SpannableTheme copyFrom) {
        return new Builder(copyFrom);
    }

    /**
     * Factory method to obtain a {@link Builder} instance initialized with default values taken
     * from current application theme.
     *
     * @param context Context to obtain default styling values (colors, etc)
     * @return {@link Builder} instance
     * @since 1.0.0
     */
    @NonNull
    public static Builder builderWithDefaults(@NonNull Context context) {

        // by default we will be using link color for the checkbox color
        // & window background as a checkMark color
        final int linkColor = resolve(context, android.R.attr.textColorLink);
        final int backgroundColor = resolve(context, android.R.attr.colorBackground);

        final Dip dip = new Dip(context);
        return new Builder()
                .linkColor(linkColor)
                .codeMultilineMargin(dip.toPx(8))
                .blockMargin(dip.toPx(24))
                .blockQuoteWidth(dip.toPx(4))
                .bulletListItemStrokeWidth(dip.toPx(1))
                .headingBreakHeight(dip.toPx(1))
                .thematicBreakHeight(dip.toPx(4))
                .tableCellPadding(dip.toPx(4))
                .tableBorderWidth(dip.toPx(1))
                .taskListDrawable(new TaskListDrawable(linkColor, linkColor, backgroundColor));
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

    protected static final int BLOCK_QUOTE_DEF_COLOR_ALPHA = 25;

    protected static final int CODE_DEF_BACKGROUND_COLOR_ALPHA = 25;
    protected static final float CODE_DEF_TEXT_SIZE_RATIO = .87F;

    protected static final int HEADING_DEF_BREAK_COLOR_ALPHA = 75;

    // taken from html spec (most browsers render headings like that)
    // is not exposed via protected modifier in order to disallow modification
    private static final float[] HEADING_SIZES = {
            2.F, 1.5F, 1.17F, 1.F, .83F, .67F,
    };

    protected static final float SCRIPT_DEF_TEXT_SIZE_RATIO = .75F;

    protected static final int THEMATIC_BREAK_DEF_ALPHA = 25;

    protected static final int TABLE_BORDER_DEF_ALPHA = 75;

    protected static final int TABLE_ODD_ROW_DEF_ALPHA = 22;

    protected final int mLinkColor;

    // used in quote, lists
    protected final int mBlockMargin;

    // by default it's 1/4th of `mBlockMargin`
    protected final int mBlockQuoteWidth;

    // by default it's mText color with `BLOCK_QUOTE_DEF_COLOR_ALPHA` applied alpha
    protected final int mBlockQuoteColor;

    // by default uses mText color (applied for un-ordered lists & ordered (bullets & numbers)
    protected final int mListItemColor;

    // by default the stroke color of a paint object
    protected final int mBulletListItemStrokeWidth;

    // width of bullet, by default min(mBlockMargin, height) / 2
    protected final int mBulletWidth;

    // by default - main mText color
    protected final int mCodeTextColor;

    // by default 0.1 alpha of textColor/mCodeTextColor
    protected final int mCodeBackgroundColor;

    // by default `width` of a space char... it's fun and games, but span doesn't have access to paint in `getLeadingMargin`
    // so, we need to set this value explicitly (think of an utility method, that takes TextView/TextPaint and measures space char)
    protected final int mCodeMultilineMargin;

    // by default Typeface.MONOSPACE
    protected final Typeface mCodeTypeface;

    // by default a bit (how much?!) smaller than normal mText
    // applied ONLY if default typeface was used, otherwise, not applied
    protected final int mCodeTextSize;

    // by default paint.getStrokeWidth
    protected final int mHeadingBreakHeight;

    // by default, mText color with `HEADING_DEF_BREAK_COLOR_ALPHA` applied alpha
    protected final int mHeadingBreakColor;

    // by default `SCRIPT_DEF_TEXT_SIZE_RATIO`
    protected final float mScriptTextSizeRatio;

    // by default textColor with `THEMATIC_BREAK_DEF_ALPHA` applied alpha
    protected final int mThematicBreakColor;

    // by default paint.strokeWidth
    protected final int mThematicBreakHeight;

    // by default 0
    protected final int mTableCellPadding;

    // by default paint.color * TABLE_BORDER_DEF_ALPHA
    protected final int mTableBorderColor;

    protected final int mTableBorderWidth;

    // by default paint.color * TABLE_ODD_ROW_DEF_ALPHA
    protected final int mTableOddRowBackgroundColor;

    // drawable that will be used to render checkbox (should be stateful)
    // TaskListDrawable can be used
    protected final Drawable mTaskListDrawable;

    protected SpannableTheme(@NonNull Builder builder) {
        mLinkColor = builder.linkColor;
        mBlockMargin = builder.blockMargin;
        mBlockQuoteWidth = builder.blockQuoteWidth;
        mBlockQuoteColor = builder.blockQuoteColor;
        mListItemColor = builder.listItemColor;
        mBulletListItemStrokeWidth = builder.bulletListItemStrokeWidth;
        mBulletWidth = builder.bulletWidth;
        mCodeTextColor = builder.codeTextColor;
        mCodeBackgroundColor = builder.codeBackgroundColor;
        mCodeMultilineMargin = builder.codeMultilineMargin;
        mCodeTypeface = builder.codeTypeface;
        mCodeTextSize = builder.codeTextSize;
        mHeadingBreakHeight = builder.headingBreakHeight;
        mHeadingBreakColor = builder.headingBreakColor;
        mScriptTextSizeRatio = builder.scriptTextSizeRatio;
        mThematicBreakColor = builder.thematicBreakColor;
        mThematicBreakHeight = builder.thematicBreakHeight;
        mTableCellPadding = builder.tableCellPadding;
        mTableBorderColor = builder.tableBorderColor;
        mTableBorderWidth = builder.tableBorderWidth;
        mTableOddRowBackgroundColor = builder.tableOddRowBackgroundColor;
        mTaskListDrawable = builder.taskListDrawable;
    }


    public void applyLinkStyle(@NonNull Paint paint) {
        paint.setUnderlineText(true);
        if (mLinkColor != 0) {
            // by default we will be using mText color
            paint.setColor(mLinkColor);
        }
    }

    public void applyBlockQuoteStyle(@NonNull Paint paint) {
        final int color;
        if (mBlockQuoteColor == 0) {
            color = ColorUtils.applyAlpha(paint.getColor(), BLOCK_QUOTE_DEF_COLOR_ALPHA);
        } else {
            color = mBlockQuoteColor;
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
    }

    public int getBlockMargin() {
        return mBlockMargin;
    }

    public int getBlockQuoteWidth() {
        final int out;
        if (mBlockQuoteWidth == 0) {
            out = (int) (mBlockMargin * .25F + .5F);
        } else {
            out = mBlockQuoteWidth;
        }
        return out;
    }

    public void applyListItemStyle(@NonNull Paint paint) {

        final int color;
        if (mListItemColor != 0) {
            color = mListItemColor;
        } else {
            color = paint.getColor();
        }
        paint.setColor(color);

        if (mBulletListItemStrokeWidth != 0) {
            paint.setStrokeWidth(mBulletListItemStrokeWidth);
        }
    }

    public int getBulletWidth(int height) {

        final int min = Math.min(mBlockMargin, height) / 2;

        final int width;
        if (mBulletWidth == 0
                || mBulletWidth > min) {
            width = min;
        } else {
            width = mBulletWidth;
        }

        return width;
    }

    public void applyCodeTextStyle(@NonNull Paint paint) {

        if (mCodeTextColor != 0) {
            paint.setColor(mCodeTextColor);
        }

        // custom typeface was set
        if (mCodeTypeface != null) {

            paint.setTypeface(mCodeTypeface);

            // please note that we won't be calculating textSize
            // (like we do when no Typeface is provided), if it's some specific typeface
            // we would confuse users about textSize
            if (mCodeTextSize != 0) {
                paint.setTextSize(mCodeTextSize);
            }

        } else {
            paint.setTypeface(Typeface.MONOSPACE);
            final float textSize;
            if (mCodeTextSize != 0) {
                textSize = mCodeTextSize;
            } else {
                textSize = paint.getTextSize() * CODE_DEF_TEXT_SIZE_RATIO;
            }
            paint.setTextSize(textSize);
        }
    }

    public int getCodeMultilineMargin() {
        return mCodeMultilineMargin;
    }

    public int getCodeBackgroundColor(@NonNull Paint paint) {
        final int color;
        if (mCodeBackgroundColor != 0) {
            color = mCodeBackgroundColor;
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
        if (mHeadingBreakColor != 0) {
            color = mHeadingBreakColor;
        } else {
            color = ColorUtils.applyAlpha(paint.getColor(), HEADING_DEF_BREAK_COLOR_ALPHA);
        }
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        if (mHeadingBreakHeight != 0) {
            //noinspection SuspiciousNameCombination
            paint.setStrokeWidth(mHeadingBreakHeight);
        }
    }

    public void applySuperScriptStyle(@NonNull TextPaint paint) {
        final float ratio;
        if (Float.compare(mScriptTextSizeRatio, .0F) == 0) {
            ratio = SCRIPT_DEF_TEXT_SIZE_RATIO;
        } else {
            ratio = mScriptTextSizeRatio;
        }
        paint.setTextSize(paint.getTextSize() * ratio);
        paint.baselineShift += (int) (paint.ascent() / 2);
    }

    public void applySubScriptStyle(@NonNull TextPaint paint) {
        final float ratio;
        if (Float.compare(mScriptTextSizeRatio, .0F) == 0) {
            ratio = SCRIPT_DEF_TEXT_SIZE_RATIO;
        } else {
            ratio = mScriptTextSizeRatio;
        }
        paint.setTextSize(paint.getTextSize() * ratio);
        paint.baselineShift -= (int) (paint.ascent() / 2);
    }

    public void applyThematicBreakStyle(@NonNull Paint paint) {
        final int color;
        if (mThematicBreakColor != 0) {
            color = mThematicBreakColor;
        } else {
            color = ColorUtils.applyAlpha(paint.getColor(), THEMATIC_BREAK_DEF_ALPHA);
        }
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        if (mThematicBreakHeight != 0) {
            //noinspection SuspiciousNameCombination
            paint.setStrokeWidth(mThematicBreakHeight);
        }
    }

    public int tableCellPadding() {
        return mTableCellPadding;
    }

    public void applyTableBorderStyle(@NonNull Paint paint) {

        final int color;
        if (mTableBorderColor == 0) {
            color = ColorUtils.applyAlpha(paint.getColor(), TABLE_BORDER_DEF_ALPHA);
        } else {
            color = mTableBorderColor;
        }

        if (mTableBorderWidth != 0) {
            paint.setStrokeWidth(mTableBorderWidth);
        }

        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void applyTableOddRowStyle(@NonNull Paint paint) {
        final int color;
        if (mTableOddRowBackgroundColor == 0) {
            color = ColorUtils.applyAlpha(paint.getColor(), TABLE_ODD_ROW_DEF_ALPHA);
        } else {
            color = mTableOddRowBackgroundColor;
        }
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * @return a Drawable to be used as a checkbox indication in task lists
     * @since 1.0.1
     */
    @Nullable
    public Drawable getTaskListDrawable() {
        return mTaskListDrawable;
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
        private int tableCellPadding;
        private int tableBorderColor;
        private int tableBorderWidth;
        private int tableOddRowBackgroundColor;
        private Drawable taskListDrawable;

        Builder() {
        }

        Builder(@NonNull SpannableTheme theme) {
            linkColor = theme.mLinkColor;
            blockMargin = theme.mBlockMargin;
            blockQuoteWidth = theme.mBlockQuoteWidth;
            blockQuoteColor = theme.mBlockQuoteColor;
            listItemColor = theme.mListItemColor;
            bulletListItemStrokeWidth = theme.mBulletListItemStrokeWidth;
            bulletWidth = theme.mBulletWidth;
            codeTextColor = theme.mCodeTextColor;
            codeBackgroundColor = theme.mCodeBackgroundColor;
            codeMultilineMargin = theme.mCodeMultilineMargin;
            codeTypeface = theme.mCodeTypeface;
            codeTextSize = theme.mCodeTextSize;
            headingBreakHeight = theme.mHeadingBreakHeight;
            headingBreakColor = theme.mHeadingBreakColor;
            scriptTextSizeRatio = theme.mScriptTextSizeRatio;
            thematicBreakColor = theme.mThematicBreakColor;
            thematicBreakHeight = theme.mThematicBreakHeight;
            tableCellPadding = theme.mTableCellPadding;
            tableBorderColor = theme.mTableBorderColor;
            tableBorderWidth = theme.mTableBorderWidth;
            tableOddRowBackgroundColor = theme.mTableOddRowBackgroundColor;
            taskListDrawable = theme.mTaskListDrawable;
        }

        @NonNull
        public Builder linkColor(@ColorInt int linkColor) {
            this.linkColor = linkColor;
            return this;
        }

        @NonNull
        public Builder blockMargin(@Dimension int blockMargin) {
            this.blockMargin = blockMargin;
            return this;
        }

        @NonNull
        public Builder blockQuoteWidth(@Dimension int blockQuoteWidth) {
            this.blockQuoteWidth = blockQuoteWidth;
            return this;
        }

        @NonNull
        public Builder blockQuoteColor(@ColorInt int blockQuoteColor) {
            this.blockQuoteColor = blockQuoteColor;
            return this;
        }

        @NonNull
        public Builder listItemColor(@ColorInt int listItemColor) {
            this.listItemColor = listItemColor;
            return this;
        }

        @NonNull
        public Builder bulletListItemStrokeWidth(@Dimension int bulletListItemStrokeWidth) {
            this.bulletListItemStrokeWidth = bulletListItemStrokeWidth;
            return this;
        }

        @NonNull
        public Builder bulletWidth(@Dimension int bulletWidth) {
            this.bulletWidth = bulletWidth;
            return this;
        }

        @NonNull
        public Builder codeTextColor(@ColorInt int codeTextColor) {
            this.codeTextColor = codeTextColor;
            return this;
        }

        @NonNull
        public Builder codeBackgroundColor(@ColorInt int codeBackgroundColor) {
            this.codeBackgroundColor = codeBackgroundColor;
            return this;
        }

        @NonNull
        public Builder codeMultilineMargin(@Dimension int codeMultilineMargin) {
            this.codeMultilineMargin = codeMultilineMargin;
            return this;
        }

        @NonNull
        public Builder codeTypeface(@NonNull Typeface codeTypeface) {
            this.codeTypeface = codeTypeface;
            return this;
        }

        @NonNull
        public Builder codeTextSize(@Dimension int codeTextSize) {
            this.codeTextSize = codeTextSize;
            return this;
        }

        @NonNull
        public Builder headingBreakHeight(@Dimension int headingBreakHeight) {
            this.headingBreakHeight = headingBreakHeight;
            return this;
        }

        @NonNull
        public Builder headingBreakColor(@ColorInt int headingBreakColor) {
            this.headingBreakColor = headingBreakColor;
            return this;
        }

        @NonNull
        public Builder scriptTextSizeRatio(@FloatRange(from = .0F, to = Float.MAX_VALUE) float scriptTextSizeRatio) {
            this.scriptTextSizeRatio = scriptTextSizeRatio;
            return this;
        }

        @NonNull
        public Builder thematicBreakColor(@ColorInt int thematicBreakColor) {
            this.thematicBreakColor = thematicBreakColor;
            return this;
        }

        @NonNull
        public Builder thematicBreakHeight(@Dimension int thematicBreakHeight) {
            this.thematicBreakHeight = thematicBreakHeight;
            return this;
        }

        @NonNull
        public Builder tableCellPadding(@Dimension int tableCellPadding) {
            this.tableCellPadding = tableCellPadding;
            return this;
        }

        @NonNull
        public Builder tableBorderColor(@ColorInt int tableBorderColor) {
            this.tableBorderColor = tableBorderColor;
            return this;
        }

        @NonNull
        public Builder tableBorderWidth(@Dimension int tableBorderWidth) {
            this.tableBorderWidth = tableBorderWidth;
            return this;
        }

        @NonNull
        public Builder tableOddRowBackgroundColor(@ColorInt int tableOddRowBackgroundColor) {
            this.tableOddRowBackgroundColor = tableOddRowBackgroundColor;
            return this;
        }

        /**
         * Supplied Drawable must be stateful ({@link Drawable#isStateful()} -> true). If a task
         * is marked as done, then this drawable will be updated with an {@code int[] { android.R.attr.state_checked }}
         * as the state, otherwise an empty array will be used. This library provides a ready to be
         * used Drawable: {@link TaskListDrawable}
         *
         * @param taskListDrawable Drawable to be used as the task list indication (checkbox)
         * @see TaskListDrawable
         * @since 1.0.1
         */
        @NonNull
        public Builder taskListDrawable(@NonNull Drawable taskListDrawable) {
            this.taskListDrawable = taskListDrawable;
            return this;
        }

        @NonNull
        public SpannableTheme build() {
            return new SpannableTheme(this);
        }
    }

    private static class Dip {

        private final float mDensity;

        Dip(@NonNull Context context) {
            mDensity = context.getResources().getDisplayMetrics().density;
        }

        int toPx(int dp) {
            return (int) (dp * mDensity + .5F);
        }
    }
}
