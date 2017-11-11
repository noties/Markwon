package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @since 1.0.1
 */
public class ImageSize {

    public enum Unit {
        PERCENT, FONT_SIZE, PIXELS
    }

    public static class Dimension {

        private final Unit unit;
        private final int value;

        public Dimension(@NonNull Unit unit, int value) {
            this.unit = unit;
            this.value = value;
        }

        @NonNull
        public Unit unit() {
            return unit;
        }

        public int value() {
            return value;
        }

        @Override
        public String toString() {
            return "Dimension{" +
                    "unit=" + unit +
                    ", value=" + value +
                    '}';
        }
    }

    // width can be relative (in percent)
    // height CANNOT be relative (endless loop)
    // both can be absolute

    private final Dimension width;
    private final Dimension height;

    public ImageSize(@Nullable Dimension width, @Nullable Dimension height) {
        this.width = width;
        this.height = height;
    }

    @Nullable
    public Dimension width() {
        return width;
    }

    @Nullable
    public Dimension height() {
        return height;
    }

    @Override
    public String toString() {
        return "ImageSize{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
