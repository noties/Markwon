package io.noties.markwon.ext.latex;

import android.graphics.Rect;
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
        return null;
    }

    @NonNull
    public static JLatexMathTheme builer() {
        return null;
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
     * @see #blockTexxtSize()
     */
    @Px
    public abstract float inlineTextSize();

    /**
     * @return text size in pixels for <strong>block LaTeX</strong>
     * @see #inlineTextSize()
     */
    @Px
    public abstract float blockTexxtSize();

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
        private float textSize;
        private float inlineTextSize;
        private float blockTextSize;

        private BackgroundProvider backgroundProvider;
        private BackgroundProvider inlineBackgroundProvider;
        private BackgroundProvider blockBackgroundProvider;

        private boolean blockFitCanvas;
        // horizontal alignment (when there is additional horizontal space)
        private int blockAlign;

        private Padding padding;
        private Padding inlinePadding;
        private Padding blockPadding;
    }
}
