package ru.noties.markwon.image;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @since 3.0.0
 */
public abstract class SchemeHandler {

    @Nullable
    public abstract ImageItem handle(@NonNull String raw, @NonNull Uri uri);
}
