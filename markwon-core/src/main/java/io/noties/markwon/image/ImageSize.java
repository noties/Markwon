package io.noties.markwon.image;

import androidx.annotation.Nullable;

/**
 * @since 1.0.1
 */
@SuppressWarnings("WeakerAccess")
public class ImageSize {

    public static class Dimension {

        public final float value;
        public final String unit;

        public Dimension(float value, @Nullable String unit) {
            this.value = value;
            this.unit = unit;
        }

        @Override
        public String toString() {
            return "Dimension{" +
                    "value=" + value +
                    ", unit='" + unit + '\'' +
                    '}';
        }
    }

    public final Dimension width;
    public final Dimension height;

    public ImageSize(@Nullable Dimension width, @Nullable Dimension height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "ImageSize{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
