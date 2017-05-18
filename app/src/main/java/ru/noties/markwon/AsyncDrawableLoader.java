package ru.noties.markwon;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.caverock.androidsvg.SVG;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import pl.droidsonroids.gif.GifDrawable;
import ru.noties.debug.Debug;
import ru.noties.markwon.spans.AsyncDrawable;

@ActivityScope
public class AsyncDrawableLoader implements AsyncDrawable.Loader {

    @Inject
    Resources resources;

    @Inject
    Picasso picasso;

    @Inject
    ExecutorService executorService;

    private final Map<String, Future<?>> requests = new HashMap<>(3);
    private final CopyOnWriteArrayList<AsyncDrawableTarget> targets = new CopyOnWriteArrayList<>();

    // sh*t..
    @Inject
    public AsyncDrawableLoader() {
    }

    @Override
    public void load(@NonNull String destination, @NonNull AsyncDrawable drawable) {

        if (destination.endsWith(".svg")) {
            // load svg
            requests.put(destination, loadSvg(destination, drawable));
        } else if (destination.endsWith(".gif")) {
            requests.put(destination, loadGif(destination, drawable));
        } else {

            final Drawable error = new ColorDrawable(0xFFff0000);
            final Drawable placeholder = new ColorDrawable(0xFF00ff00);
            error.setBounds(0, 0, 100, 100);
            placeholder.setBounds(0, 0, 50, 50);

            final AsyncDrawableTarget target = new AsyncDrawableTarget(resources, drawable, new AsyncDrawableTarget.DoneListener() {
                @Override
                public void onLoadingDone(AsyncDrawableTarget target) {
                    targets.remove(target);
                }
            });

            targets.add(target);

            picasso
                    .load(destination)
                    .tag(destination)
                    .placeholder(placeholder)
                    .error(error)
                    .into(target);
        }
    }

    @Override
    public void cancel(@NonNull String destination) {
        Debug.i("destination: %s", destination);
        picasso.cancelTag(destination);

        final Future<?> future = requests.remove(destination);
        if (future != null) {
            future.cancel(true);
        }
    }

    private Future<?> loadSvg(final String destination, final AsyncDrawable asyncDrawable) {
        return executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {

                    final URL url = new URL(destination);
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    final InputStream inputStream = connection.getInputStream();

                    final SVG svg = SVG.getFromInputStream(inputStream);
                    final float w = svg.getDocumentWidth();
                    final float h = svg.getDocumentHeight();
                    Debug.i("w: %s, h: %s", w, h);

                    final float density = resources.getDisplayMetrics().density;
                    Debug.i(density);

                    final int width = (int) (w * density + .5F);
                    final int height = (int) (h * density + .5F);
                    final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                    final Canvas canvas = new Canvas(bitmap);
                    canvas.scale(density, density);
                    svg.renderToCanvas(canvas);

                    final Drawable drawable = new BitmapDrawable(resources, bitmap);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    asyncDrawable.setResult(drawable);

                } catch (Throwable t) {
                    Debug.e(t);
                }
            }
        });
    }

    private Future<?> loadGif(final String destination, final AsyncDrawable asyncDrawable) {
        return executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {

                    final URL url = new URL(destination);
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    final InputStream inputStream = connection.getInputStream();

                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final byte[] buffer = new byte[1024 * 8];
                    int read;
                    while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
                        baos.write(buffer, 0, read);
                    }
                    final GifDrawable drawable = new GifDrawable(baos.toByteArray());
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    asyncDrawable.setResult(drawable);
                } catch (Throwable t) {
                    Debug.e(t);
                }
            }
        });
    }
}
