package io.noties.markwon.app;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import io.noties.debug.Debug;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@ActivityScope
public class MarkdownLoader {

    public interface OnMarkdownTextLoaded {
        void apply(String text);
    }

    @Inject
    Context context;

    @Inject
    ExecutorService service;

    @Inject
    Handler handler;

    @Inject
    OkHttpClient client;

    private Future<?> task;

    @Inject
    MarkdownLoader() {
    }

    public void load(@Nullable final Uri uri, @NonNull final OnMarkdownTextLoaded loaded) {
        cancel();
        task = service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    deliver(loaded, text(uri));
                } catch (Throwable t) {
                    Debug.e(t);
                }
            }
        });
    }

    public void cancel() {
        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }

    private boolean isCancelled() {
        return task == null || task.isCancelled();
    }

    private void deliver(@NonNull final OnMarkdownTextLoaded loaded, final String text) {
        if (!isCancelled()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // as this call is async, we need to check again if we are cancelled
                    if (!isCancelled()) {
                        loaded.apply(text);
                        task = null;
                    }
                }
            });
        }
    }

    private String text(@Nullable Uri uri) {
        final String out;
        if (uri == null) {
            out = loadReadMe();
        } else {
            out = loadExternalResource(uri);
        }
        return out;
    }

    private String loadReadMe() {
        InputStream stream = null;
        try {
            stream = context.getAssets().open("README.md");
        } catch (IOException e) {
            Debug.e(e);
        }
        return readStream(stream);
    }

    private String loadExternalResource(@NonNull Uri uri) {
        final String out;
        final String scheme = uri.getScheme();
        if (!TextUtils.isEmpty(scheme) && ContentResolver.SCHEME_FILE.equals(scheme)) {
            out = loadExternalFile(uri);
        } else {
            out = loadExternalUrl(uri);
        }
        return out;
    }

    private String loadExternalFile(@NonNull Uri uri) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(uri.getPath()));
        } catch (FileNotFoundException e) {
            Debug.e(e);
        }
        return readStream(stream);
    }

    private String loadExternalUrl(@NonNull Uri uri) {

        final Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Debug.e(e);
        }

        final ResponseBody body = response != null
                ? response.body()
                : null;

        String out = null;

        if (body != null) {
            try {
                out = body.string();
            } catch (IOException e) {
                Debug.e(e);
            }
        }

        return out;
    }

    private static String readStream(@Nullable InputStream inputStream) {

        String out = null;

        if (inputStream != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                final StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line)
                            .append('\n');
                }
                out = builder.toString();
            } catch (IOException e) {
                Debug.e(e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // no op
                    }
                }
            }
        }

        return out;
    }
}
