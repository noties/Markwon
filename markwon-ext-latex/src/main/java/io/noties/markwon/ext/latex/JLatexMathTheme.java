package io.noties.markwon.ext.latex;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import ru.noties.jlatexmath.JLatexMathDrawable;

/**
 * @since 4.3.0-SNAPSHOT
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
     * Moved from {@link JLatexMathPlugin} in {@code 4.3.0-SNAPSHOT} version
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
    }
}
