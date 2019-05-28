package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;

/**
 * @since 2.0.0
 */
public abstract class ImageItem {

    /**
     * Create an {@link ImageItem} with result, so no further decoding is required.
     *
     * @since 4.0.0-SNAPSHOT
     */
    @NonNull
    public static ImageItem withResult(@Nullable Drawable drawable) {
        return new WithResult(drawable);
    }

    @NonNull
    public static ImageItem withDecodingNeeded(
            @Nullable String contentType,
            @Nullable InputStream inputStream) {
        return new WithDecodingNeeded(contentType, inputStream);
    }

    private ImageItem() {
    }

    public static class WithResult extends ImageItem {

        private final Drawable result;

        WithResult(@Nullable Drawable drawable) {
            result = drawable;
        }

        @Nullable
        public Drawable result() {
            return result;
        }
    }

    public static class WithDecodingNeeded extends ImageItem {

        private final String contentType;
        private final InputStream inputStream;

        WithDecodingNeeded(
                @Nullable String contentType,
                @Nullable InputStream inputStream) {
            this.contentType = contentType;
            this.inputStream = inputStream;
        }

        @Nullable
        public String contentType() {
            return contentType;
        }

        @Nullable
        public InputStream inputStream() {
            return inputStream;
        }
    }
}
