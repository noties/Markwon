package io.noties.markwon.image.gif;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import io.noties.markwon.image.MediaDecoder;
import pl.droidsonroids.gif.GifDrawable;
import io.noties.markwon.image.DrawableUtils;

/**
 * @since 1.1.0
 */
@SuppressWarnings("WeakerAccess")
public class GifMediaDecoder extends MediaDecoder {

    public static final String CONTENT_TYPE = "image/gif";

    @NonNull
    public static GifMediaDecoder create(boolean autoPlayGif) {
        return new GifMediaDecoder(autoPlayGif);
    }

    private final boolean autoPlayGif;

    protected GifMediaDecoder(boolean autoPlayGif) {
        this.autoPlayGif = autoPlayGif;

        // @since 4.0.0-SNAPSHOT
        Holder.validate();
    }

    @NonNull
    @Override
    public Drawable decode(@Nullable String contentType, @NonNull InputStream inputStream) {

        final byte[] bytes;
        try {
            bytes = readBytes(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read GIF input-stream", e);
        }

        final GifDrawable drawable;
        try {
            drawable = newGifDrawable(bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Exception creating GifDrawable", e);
        }

        DrawableUtils.applyIntrinsicBounds(drawable);

        if (!autoPlayGif) {
            drawable.pause();
        }

        return drawable;
    }

    @NonNull
    @Override
    public Collection<String> supportedTypes() {
        return Collections.singleton(CONTENT_TYPE);
    }

    @NonNull
    protected GifDrawable newGifDrawable(@NonNull byte[] bytes) throws IOException {
        return new GifDrawable(bytes);
    }

    @NonNull
    protected static byte[] readBytes(@NonNull InputStream stream) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final int length = 1024 * 8;
        final byte[] buffer = new byte[length];
        int read;
        while ((read = stream.read(buffer, 0, length)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        return outputStream.toByteArray();
    }

    // @since 4.0.0-SNAPSHOT
    private static class Holder {

        private static final boolean HAS_GIF;

        static {
            boolean result = true;
            try {
                GifDrawable.class.getName();
            } catch (Throwable t) {
                result = false;
                t.printStackTrace();
            }
            HAS_GIF = result;
        }

        static void validate() {
            if (!HAS_GIF) {
                throw new IllegalStateException("`pl.droidsonroids.gif:android-gif-drawable:*` " +
                        "dependency is missing, please add to your project explicitly");
            }
        }
    }
}
