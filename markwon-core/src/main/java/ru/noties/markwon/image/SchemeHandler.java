package ru.noties.markwon.image;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @since 3.0.0
 */
public abstract class SchemeHandler {

    @Nullable
    public abstract ImageItem handle(@NonNull String raw, @NonNull Uri uri);
}
