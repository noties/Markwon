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

/**
 * A simple network scheme handler that is not dependent on any external libraries.
 *
 * @see #create()
 * @since 3.0.0
 */
public class NetworkSchemeHandler extends SchemeHandler {

    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";

    @NonNull
    public static NetworkSchemeHandler create() {
        return new NetworkSchemeHandler();
    }

    @NonNull
    @Override
    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {

        final ImageItem imageItem;
        try {

            final URL url = new URL(raw);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            final int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                final String contentType = contentType(connection.getHeaderField("Content-Type"));
                final InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                imageItem = ImageItem.withDecodingNeeded(contentType, inputStream);
            } else {
                throw new IOException("Bad response code: " + responseCode + ", url: " + raw);
            }

        } catch (IOException e) {
            throw new IllegalStateException("Exception obtaining network resource: " + raw, e);
        }

        return imageItem;
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
