package io.noties.markwon.image;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.util.Collection;

/**
 * @since 3.0.0
 */
public abstract class MediaDecoder {

    /**
     * Changes since 4.0.0-SNAPSHOT:
     * <ul>
     * <li>Returns `non-null` drawable</li>
     * <li>Added `contentType` method parameter</li>
     * </ul>
     */
    @NonNull
    public abstract Drawable decode(
            @Nullable String contentType,
            @NonNull InputStream inputStream
    );

    /**
     * @since 4.0.0-SNAPSHOT
     */
    @NonNull
    public abstract Collection<String> supportedTypes();
}
