package ru.noties.markwon.il;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.InputStream;

/**
 * @since 1.1.0
 */
public class SvgMediaDecoder extends MediaDecoder {

    private static final String CONTENT_TYPE_SVG = "image/svg+xml";
    private static final String FILE_EXTENSION_SVG = ".svg";

    @NonNull
    public static SvgMediaDecoder create(@NonNull Resources resources) {
        return new SvgMediaDecoder(resources);
    }

    private final Resources resources;

    SvgMediaDecoder(Resources resources) {
        this.resources = resources;
    }

    @Override
    public boolean canDecodeByContentType(@Nullable String contentType) {
        return contentType != null && contentType.startsWith(CONTENT_TYPE_SVG);
    }

    @Override
    public boolean canDecodeByFileName(@NonNull String fileName) {
        return fileName.endsWith(FILE_EXTENSION_SVG);
    }

    @Nullable
    @Override
    public Drawable decode(@NonNull InputStream inputStream) {

        final Drawable out;

        SVG svg = null;
        try {
            svg = SVG.getFromInputStream(inputStream);
        } catch (SVGParseException e) {
            e.printStackTrace();
        }

        if (svg == null) {
            out = null;
        } else {

            final float w = svg.getDocumentWidth();
            final float h = svg.getDocumentHeight();
            final float density = resources.getDisplayMetrics().density;

            final int width = (int) (w * density + .5F);
            final int height = (int) (h * density + .5F);

            final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            final Canvas canvas = new Canvas(bitmap);
            canvas.scale(density, density);
            svg.renderToCanvas(canvas);

            out = new BitmapDrawable(resources, bitmap);
            DrawableUtils.intrinsicBounds(out);
        }

        return out;
    }
}
