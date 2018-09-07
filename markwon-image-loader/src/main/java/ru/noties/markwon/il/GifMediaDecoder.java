package ru.noties.markwon.il;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;

/**
 * @since 1.1.0
 */
@SuppressWarnings("WeakerAccess")
public class GifMediaDecoder extends MediaDecoder {

    protected static final String CONTENT_TYPE_GIF = "image/gif";
    protected static final String FILE_EXTENSION_GIF = ".gif";

    @NonNull
    public static GifMediaDecoder create(boolean autoPlayGif) {
        return new GifMediaDecoder(autoPlayGif);
    }

    private final boolean autoPlayGif;

    protected GifMediaDecoder(boolean autoPlayGif) {
        this.autoPlayGif = autoPlayGif;
    }

    @Override
    public boolean canDecodeByContentType(@Nullable String contentType) {
        return CONTENT_TYPE_GIF.equals(contentType);
    }

    @Override
    public boolean canDecodeByFileName(@NonNull String fileName) {
        return fileName.endsWith(FILE_EXTENSION_GIF);
    }

    @Nullable
    @Override
    public Drawable decode(@NonNull InputStream inputStream) {

        Drawable out = null;

        final byte[] bytes = readBytes(inputStream);
        if (bytes != null) {
            try {
                out = newGifDrawable(bytes);
                DrawableUtils.intrinsicBounds(out);

                if (!autoPlayGif) {
                    ((GifDrawable) out).pause();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return out;
    }

    @NonNull
    protected Drawable newGifDrawable(@NonNull byte[] bytes) throws IOException {
        return new GifDrawable(bytes);
    }

    @Nullable
    protected static byte[] readBytes(@NonNull InputStream stream) {

        byte[] out = null;

        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int length = 1024 * 8;
            final byte[] buffer = new byte[length];
            int read;
            while ((read = stream.read(buffer, 0, length)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            out = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }
}
