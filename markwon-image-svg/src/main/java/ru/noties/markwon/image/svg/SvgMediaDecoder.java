package ru.noties.markwon.image.svg;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.InputStream;

import ru.noties.markwon.image.DrawableUtils;

/**
 * @since 1.1.0
 */
public class SvgMediaDecoder extends MediaDecoder {

    public static final String CONTENT_TYPE = "image/svg+xml";

    @NonNull
    public static SvgMediaDecoder create(@NonNull Resources resources) {
        return new SvgMediaDecoder(resources);
    }

    private final Resources resources;

    @SuppressWarnings("WeakerAccess")
    SvgMediaDecoder(Resources resources) {
        this.resources = resources;
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
            DrawableUtils.applyIntrinsicBounds(out);
        }

        return out;
    }
}
