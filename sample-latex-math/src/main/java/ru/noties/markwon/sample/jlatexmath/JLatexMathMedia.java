package ru.noties.markwon.sample.jlatexmath;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import ru.noties.jlatexmath.JLatexMathDrawable;
import ru.noties.markwon.il.ImageItem;
import ru.noties.markwon.il.MediaDecoder;
import ru.noties.markwon.il.SchemeHandler;

public class JLatexMathMedia {

    public static class Config {

        protected final float textSize;

        protected Drawable background;

        @JLatexMathDrawable.Align
        protected int align = JLatexMathDrawable.ALIGN_CENTER;

        protected boolean fitCanvas = true;

        protected int padding;

        public Config(float textSize) {
            this.textSize = textSize;
        }
    }

    public static final String SCHEME = "jlatexmath";

    @NonNull
    public static String makeDestination(@NonNull String latex) {
        return SCHEME + "://" + latex;
    }

    private static final String CONTENT_TYPE = "text/jlatexmath";

    private final Config config;

    public JLatexMathMedia(@NonNull Config config) {
        this.config = config;
    }

    @NonNull
    public SchemeHandler schemeHandler() {
        return new SchemeHandlerImpl();
    }

    @NonNull
    public MediaDecoder mediaDecoder() {
        return new MediaDecoderImpl(config);
    }

    static class SchemeHandlerImpl extends SchemeHandler {

        @Nullable
        @Override
        public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {

            Log.e("handle", raw);

            ImageItem item = null;

            try {
                final byte[] bytes = raw.substring(SCHEME.length()).getBytes("UTF-8");
                item = new ImageItem(
                        CONTENT_TYPE,
                        new ByteArrayInputStream(bytes),
                        null
                );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return item;
        }

        @Override
        public void cancel(@NonNull String raw) {
            // no op
        }
    }

    static class MediaDecoderImpl extends MediaDecoder {

        private final Config config;

        MediaDecoderImpl(@NonNull Config config) {
            this.config = config;
        }

        @Override
        public boolean canDecodeByContentType(@Nullable String contentType) {
            return CONTENT_TYPE.equals(contentType);
        }

        @Override
        public boolean canDecodeByFileName(@NonNull String fileName) {
            return false;
        }

        @Nullable
        @Override
        public Drawable decode(@NonNull InputStream inputStream) {

            final Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
            final String latex = scanner.hasNext()
                    ? scanner.next()
                    : null;

            Log.e("decode", latex);

            if (latex == null) {
                return null;
            }

            // todo: change to float
            return JLatexMathDrawable.builder(latex)
                    .textSize((int) config.textSize)
                    .background(config.background)
                    .align(config.align)
                    .fitCanvas(config.fitCanvas)
                    .padding(config.padding)
                    .build();
        }
    }
}
