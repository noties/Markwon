package ru.noties.markwon.image;

import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * @since 3.0.0
 */
public abstract class SchemeHandler {

    /**
     * Changes since 4.0.0-SNAPSHOT:
     * <ul>
     * <li>Returns `non-null` image-item</li>
     * <li>added `throws Exception` to method signature</li>
     * </ul>
     *
     * @throws Exception since 4.0.0-SNAPSHOT
     * @see ImageItem#withResult(android.graphics.drawable.Drawable)
     * @see ImageItem#withDecodingNeeded(String, java.io.InputStream)
     */
    @NonNull
    public abstract ImageItem handle(@NonNull String raw, @NonNull Uri uri) throws Exception;
}
