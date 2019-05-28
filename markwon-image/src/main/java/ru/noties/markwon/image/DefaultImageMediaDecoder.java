package ru.noties.markwon.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

/**
 * This class can be used as the last {@link MediaDecoder} to _try_ to handle all rest cases.
 * Here we just assume that supplied InputStream is of image type and try to decode it.
 *
 * @since 1.1.0
 */
public class DefaultImageMediaDecoder extends MediaDecoder {

    @NonNull
    public static DefaultImageMediaDecoder create() {
        return new DefaultImageMediaDecoder(Resources.getSystem());
    }

    @NonNull
    public static DefaultImageMediaDecoder create(@NonNull Resources resources) {
        return new DefaultImageMediaDecoder(resources);
    }

    private final Resources resources;

    @SuppressWarnings("WeakerAccess")
    DefaultImageMediaDecoder(Resources resources) {
        this.resources = resources;
    }

    @NonNull
    @Override
    public Drawable decode(@Nullable String contentType, @NonNull InputStream inputStream) {

        final Bitmap bitmap;
        try {
            // absolutely not optimal... thing
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Throwable t) {
            throw new IllegalStateException("Exception decoding input-stream", t);
        }

        final Drawable drawable = new BitmapDrawable(resources, bitmap);
        DrawableUtils.applyIntrinsicBounds(drawable);
        return drawable;
    }

    @NonNull
    @Override
    public Collection<String> supportedTypes() {
        return Collections.emptySet();
    }
}
