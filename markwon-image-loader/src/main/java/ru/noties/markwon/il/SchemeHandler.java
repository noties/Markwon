package ru.noties.markwon.il;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;

/**
 * @since 2.0.0
 */
public abstract class SchemeHandler {

    @Nullable
    public abstract ImageItem handle(@NonNull String raw, @NonNull Uri uri);

    public abstract void cancel(@NonNull String raw);

    @NonNull
    public abstract Collection<String> schemes();
}
