package ru.noties.markwon.image.okhttp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.noties.markwon.image.ImageItem;
import ru.noties.markwon.image.SchemeHandler;

class OkHttpSchemeHandler extends SchemeHandler {

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private final Call.Factory callFactory;

    OkHttpSchemeHandler(@NonNull Call.Factory callFactory) {
        this.callFactory = callFactory;
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
            response = callFactory.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            final ResponseBody body = response.body();
            if (body != null) {
                final InputStream inputStream = body.byteStream();
                if (inputStream != null) {
                    final String contentType = response.header(HEADER_CONTENT_TYPE);
                    out = new ImageItem(contentType, inputStream);
                }
            }
        }

        return out;
    }
}
