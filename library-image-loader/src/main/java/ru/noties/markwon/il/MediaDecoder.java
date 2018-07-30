package ru.noties.markwon.il;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;

/**
 * @since 1.1.0
 */
public abstract class MediaDecoder {

    public abstract boolean canDecodeByContentType(@Nullable String contentType);

    public abstract boolean canDecodeByFileName(@NonNull String fileName);

    @Nullable
    public abstract Drawable decode(@NonNull InputStream inputStream);
}
