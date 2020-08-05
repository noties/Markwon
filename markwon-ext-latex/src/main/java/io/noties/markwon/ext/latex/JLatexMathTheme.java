package io.noties.markwon.ext.latex;

import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import ru.noties.jlatexmath.JLatexMathDrawable;

/**
 * @since 4.3.0
 */
public abstract class JLatexMathTheme {

    @NonNull
    public static JLatexMathTheme create(@Px float textSize) {
        return builder(textSize).build();
    }

    @NonNull
    public static JLatexMathTheme create(@Px float inlineTextSize, @Px float blockTextSize) {
        return builder(inlineTextSize, blockTextSize).build();
    }

    @NonNull
    public static JLatexMathTheme.Builder builder(@Px float textSize) {
        return new JLatexMathTheme.Builder(textSize, 0F, 0F);
    }

    @NonNull
    public static JLatexMathTheme.Builder builder(@Px float inlineTextSize, @Px float blockTextSize) {
        return new Builder(0F, inlineTextSize, blockTextSize);
    }

    /**
     * Moved from {@link JLatexMathPlugin} in {@code 4.3.0} version
     *
     * @since 4.0.0
     */
    public interface BackgroundProvider {
        @NonNull
        Drawable provide();
    }

    /**
     * Special immutable class to hold padding information
     */
    @SuppressWarnings("WeakerAccess")
    public static class Padding {
        public final int left;
        public final int top;
        public final int right;
        public final int bottom;

        public Padding(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        @NonNull
        @Override
        public String toString() {
            return "Padding{" +
                    "left=" + left +
                    ", top=" + top +
                    ", right=" + right +
                    ", bottom=" + bottom +
                    '}';
        }

        @NonNull
        public static Padding all(int value) {
            return new Padding(value, value, value, value);
        }

        @NonNull
        public static Padding symmetric(int vertical, int horizontal) {
            return new Padding(horizontal, vertical, horizontal, vertical);
        }

        /**
         * @since 4.5.0
         */
        @NonNull
        public static Padding of(int left, int top, int right, int bottom) {
            return new Padding(left, top, right, bottom);
        }
    }

    /**
     * @return text size in pixels for <strong>inline LaTeX</strong>
     * @see #blockTextSize()
     */
    @Px
    public abstract float inlineTextSize();

    /**
     * @return text size in pixels for <strong>block LaTeX</strong>
     * @see #inlineTextSize()
     */
    @Px
    public abstract float blockTextSize();

    @Nullable
    public abstract BackgroundProvider inlineBackgroundProvider();

    @Nullable
    public abstract BackgroundProvider blockBackgroundProvider();

    /**
     * @return boolean if <strong>block LaTeX</strong> must fit the width of canvas
     */
    public abstract boolean blockFitCanvas();

    /**
     * @return horizontal alignment of <strong>block LaTeX</strong> if {@link #blockFitCanvas()}
     * is enabled (thus space for alignment is available)
     */
    @JLatexMathDrawable.Align
    public abstract int blockHorizontalAlignment();

    @Nullable
    public abstract Padding inlinePadding();

    @Nullable
    public abstract Padding blockPadding();

    @ColorInt
    public abstract int inlineTextColor();

    @ColorInt
    public abstract int blockTextColor();

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public static class Builder {
        private final float textSize;
        private final float inlineTextSize;
        private final float blockTextSize;

        private BackgroundProvider backgroundProvider;
        private BackgroundProvider inlineBackgroundProvider;
        private BackgroundProvider blockBackgroundProvider;

        private boolean blockFitCanvas = true;
        // horizontal alignment (when there is additional horizontal space)
        private int blockHorizontalAlignment = JLatexMathDrawable.ALIGN_CENTER;

        private Padding padding;
        private Padding inlinePadding;
        private Padding blockPadding;

        private int textColor;
        private int inlineTextColor;
        private int blockTextColor;

        Builder(float textSize, float inlineTextSize, float blockTextSize) {
            this.textSize = textSize;
            this.inlineTextSize = inlineTextSize;
            this.blockTextSize = blockTextSize;
        }

        @NonNull
        public Builder backgroundProvider(@Nullable BackgroundProvider backgroundProvider) {
            this.backgroundProvider = backgroundProvider;
            this.inlineBackgroundProvider = backgroundProvider;
            this.blockBackgroundProvider = backgroundProvider;
            return this;
        }

        @NonNull
        public Builder inlineBackgroundProvider(@Nullable BackgroundProvider inlineBackgroundProvider) {
            this.inlineBackgroundProvider = inlineBackgroundProvider;
            return this;
        }

        @NonNull
        public Builder blockBackgroundProvider(@Nullable BackgroundProvider blockBackgroundProvider) {
            this.blockBackgroundProvider = blockBackgroundProvider;
            return this;
        }

        /**
         * Configure if `LaTeX` formula should take all available widget width.
         * By default - `true`
         */
        @NonNull
        public Builder blockFitCanvas(boolean blockFitCanvas) {
            this.blockFitCanvas = blockFitCanvas;
            return this;
        }

        @NonNull
        public Builder blockHorizontalAlignment(@JLatexMathDrawable.Align int blockHorizontalAlignment) {
            this.blockHorizontalAlignment = blockHorizontalAlignment;
            return this;
        }

        @NonNull
        public Builder padding(@Nullable Padding padding) {
            this.padding = padding;
            this.inlinePadding = padding;
            this.blockPadding = padding;
            return this;
        }

        @NonNull
        public Builder inlinePadding(@Nullable Padding inlinePadding) {
            this.inlinePadding = inlinePadding;
            return this;
        }

        @NonNull
        public Builder blockPadding(@Nullable Padding blockPadding) {
            this.blockPadding = blockPadding;
            return this;
        }

        @NonNull
        public Builder textColor(@ColorInt int textColor) {
            this.textColor = textColor;
            return this;
        }

        @NonNull
        public Builder inlineTextColor(@ColorInt int inlineTextColor) {
            this.inlineTextColor = inlineTextColor;
            return this;
        }

        @NonNull
        public Builder blockTextColor(@ColorInt int blockTextColor) {
            this.blockTextColor = blockTextColor;
            return this;
        }

        @NonNull
        public JLatexMathTheme build() {
            return new Impl(this);
        }
    }

    static class Impl extends JLatexMathTheme {

        private final float textSize;
        private final float inlineTextSize;
        private final float blockTextSize;

        private final BackgroundProvider backgroundProvider;
        private final BackgroundProvider inlineBackgroundProvider;
        private final BackgroundProvider blockBackgroundProvider;

        private final boolean blockFitCanvas;
        // horizontal alignment (when there is additional horizontal space)
        private int blockHorizontalAlignment;

        private final Padding padding;
        private final Padding inlinePadding;
        private final Padding blockPadding;

        private final int textColor;
        private final int inlineTextColor;
        private final int blockTextColor;

        Impl(@NonNull Builder builder) {
            this.textSize = builder.textSize;
            this.inlineTextSize = builder.inlineTextSize;
            this.blockTextSize = builder.blockTextSize;
            this.backgroundProvider = builder.backgroundProvider;
            this.inlineBackgroundProvider = builder.inlineBackgroundProvider;
            this.blockBackgroundProvider = builder.blockBackgroundProvider;
            this.blockFitCanvas = builder.blockFitCanvas;
            this.blockHorizontalAlignment = builder.blockHorizontalAlignment;
            this.padding = builder.padding;
            this.inlinePadding = builder.inlinePadding;
            this.blockPadding = builder.blockPadding;
            this.textColor = builder.textColor;
            this.inlineTextColor = builder.inlineTextColor;
            this.blockTextColor = builder.blockTextColor;
        }

        @Override
        public float inlineTextSize() {
            if (inlineTextSize > 0F) {
                return inlineTextSize;
            }
            return textSize;
        }

        @Override
        public float blockTextSize() {
            if (blockTextSize > 0F) {
                return blockTextSize;
            }
            return textSize;
        }

        @Nullable
        @Override
        public BackgroundProvider inlineBackgroundProvider() {
            if (inlineBackgroundProvider != null) {
                return inlineBackgroundProvider;
            }
            return backgroundProvider;
        }

        @Nullable
        @Override
        public BackgroundProvider blockBackgroundProvider() {
            if (blockBackgroundProvider != null) {
                return blockBackgroundProvider;
            }
            return backgroundProvider;
        }

        @Override
        public boolean blockFitCanvas() {
            return blockFitCanvas;
        }

        @Override
        public int blockHorizontalAlignment() {
            return blockHorizontalAlignment;
        }

        @Nullable
        @Override
        public Padding inlinePadding() {
            if (inlinePadding != null) {
                return inlinePadding;
            }
            return padding;
        }

        @Nullable
        @Override
        public Padding blockPadding() {
            if (blockPadding != null) {
                return blockPadding;
            }
            return padding;
        }

        @Override
        public int inlineTextColor() {
            if (inlineTextColor != 0) {
                return inlineTextColor;
            }
            return textColor;
        }

        @Override
        public int blockTextColor() {
            if (blockTextColor != 0) {
                return blockTextColor;
            }
            return textColor;
        }
    }
}
