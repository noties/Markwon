package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;

/**
 * @since 3.0.0
 */
public abstract class MediaDecoder {

    @Nullable
    public abstract Drawable decode(@NonNull InputStream inputStream);
}
