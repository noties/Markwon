package ru.noties.markwon.image;

import android.support.annotation.Nullable;

import java.io.InputStream;

/**
 * @since 2.0.0
 */
public class ImageItem {

    private final String contentType;
    private final InputStream inputStream;

    public ImageItem(
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
