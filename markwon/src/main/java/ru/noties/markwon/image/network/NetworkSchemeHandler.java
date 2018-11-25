package ru.noties.markwon.image.network;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.noties.markwon.image.ImageItem;
import ru.noties.markwon.image.SchemeHandler;

public class NetworkSchemeHandler extends SchemeHandler {

    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";

    @NonNull
    public static NetworkSchemeHandler create() {
        return new NetworkSchemeHandler();
    }

    @Nullable
    @Override
    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {

        try {

            final URL url = new URL(raw);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            final int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                final String contentType = contentType(connection.getHeaderField("Content-Type"));
                final InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                return new ImageItem(contentType, inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    static String contentType(@Nullable String contentType) {

        if (contentType == null) {
            return null;
        }

        final int index = contentType.indexOf(';');
        if (index > -1) {
            return contentType.substring(0, index);
        }

        return contentType;
    }
}
