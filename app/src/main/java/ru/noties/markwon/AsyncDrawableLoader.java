package ru.noties.markwon;

import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;
import pl.droidsonroids.gif.GifDrawable;
import ru.noties.debug.Debug;
import ru.noties.markwon.spans.AsyncDrawable;

public class AsyncDrawableLoader implements AsyncDrawable.Loader {

    private final TextView view;
    private final Picasso picasso;
    private final OkHttpClient client;
    private final ExecutorService executorService;
    private final Map<String, Future<?>> requests;

    // sh*t..
    public AsyncDrawableLoader(TextView view) {
        this.view = view;
        this.picasso = new Picasso.Builder(view.getContext())
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Debug.e(exception, picasso, uri);
                    }
                })
                .build();
        this.client = new OkHttpClient();
        this.executorService = Executors.newCachedThreadPool();
        this.requests = new HashMap<>(3);
    }

    @Override
    public void load(@NonNull String destination, @NonNull AsyncDrawable drawable) {

        Debug.i("destination: %s", destination);

        if (destination.endsWith(".svg")) {
            // load svg
            requests.put(destination, loadSvg(destination, drawable));
        } else if (destination.endsWith(".gif")) {
            requests.put(destination, loadGif(destination, drawable));
        } else {
            picasso
                    .load(destination)
                    .tag(destination)
                    .into(new TextViewTarget(view, drawable));
        }
    }

    @Override
    public void cancel(@NonNull String destination) {
        Debug.i("destination: %s", destination);
        picasso.cancelTag(destination);

        final Future<?> future = requests.get(destination);
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
                    final Picture picture = svg.renderToPicture();
                    final Drawable drawable = new PictureDrawable(picture);
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
