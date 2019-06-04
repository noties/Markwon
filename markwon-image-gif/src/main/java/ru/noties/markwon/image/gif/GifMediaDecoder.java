package ru.noties.markwon.image.gif;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;
import ru.noties.markwon.image.DrawableUtils;
import ru.noties.markwon.image.MediaDecoder;

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
    }

    @Nullable
    @Override
    public Drawable decode(@NonNull InputStream inputStream) {

        Drawable out = null;

        final byte[] bytes = readBytes(inputStream);
        if (bytes != null) {
            try {
                out = newGifDrawable(bytes);
                DrawableUtils.applyIntrinsicBounds(out);

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
