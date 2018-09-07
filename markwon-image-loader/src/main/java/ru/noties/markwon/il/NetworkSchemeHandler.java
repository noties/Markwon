package ru.noties.markwon.il;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @since 2.0.0
 */
public class NetworkSchemeHandler extends SchemeHandler {

    @NonNull
    public static NetworkSchemeHandler create(@NonNull OkHttpClient client) {
        return new NetworkSchemeHandler(client);
    }

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private final OkHttpClient client;

    @SuppressWarnings("WeakerAccess")
    NetworkSchemeHandler(@NonNull OkHttpClient client) {
        this.client = client;
    }

    @Nullable
    @Override
    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {

        ImageItem out = null;

        final Request request = new Request.Builder()
                .url(raw)
                .tag(raw)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            final ResponseBody body = response.body();
            if (body != null) {
                final InputStream inputStream = body.byteStream();
                if (inputStream != null) {
                    final String contentType = response.header(HEADER_CONTENT_TYPE);
                    out = new ImageItem(contentType, inputStream, null);
                }
            }
        }

        return out;
    }

    @Override
    public void cancel(@NonNull String raw) {
        final List<Call> calls = client.dispatcher().queuedCalls();
        if (calls != null) {
            for (Call call : calls) {
                if (!call.isCanceled()) {
                    if (raw.equals(call.request().tag())) {
                        call.cancel();
                    }
                }
            }
        }
    }
}
