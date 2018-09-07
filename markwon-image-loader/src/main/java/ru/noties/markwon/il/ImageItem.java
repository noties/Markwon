package ru.noties.markwon.il;

import android.support.annotation.Nullable;

import java.io.InputStream;

/**
 * @since 2.0.0
 */
public class ImageItem {

    private final String contentType;
    private final InputStream inputStream;
    private final String fileName;

    public ImageItem(
            @Nullable String contentType,
            @Nullable InputStream inputStream,
            @Nullable String fileName) {
        this.contentType = contentType;
        this.inputStream = inputStream;
        this.fileName = fileName;
    }

    @Nullable
    public String contentType() {
        return contentType;
    }

    @Nullable
    public InputStream inputStream() {
        return inputStream;
    }

    @Nullable
    public String fileName() {
        return fileName;
    }
}
