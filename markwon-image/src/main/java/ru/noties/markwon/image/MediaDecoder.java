package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;

/**
 * @since 3.0.0
 */
public abstract class MediaDecoder {

    /**
     * Changes since 4.0.0-SNAPSHOT:
     * <ul>
     * <li>Returns `non-null` drawable</li>
     * <li>Added `contentType` method parameter</li>
     * <li>Added `throws Exception` to method signature</li>
     * </ul>
     *
     * @throws Exception since 4.0.0-SNAPSHOT
     */
    @NonNull
    public abstract Drawable decode(
            @Nullable String contentType,
            @NonNull InputStream inputStream
    ) throws Exception;
}
