package ru.noties.markwon.il;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;

/**
 * @since 2.0.0
 */
public class DataUriSchemeHandler extends SchemeHandler {

    @NonNull
    public static DataUriSchemeHandler create() {
        return new DataUriSchemeHandler(DataUriParser.create(), DataUriDecoder.create());
    }

    private final DataUriParser uriParser;
    private final DataUriDecoder uriDecoder;

    @SuppressWarnings("WeakerAccess")
    DataUriSchemeHandler(@NonNull DataUriParser uriParser, @NonNull DataUriDecoder uriDecoder) {
        this.uriParser = uriParser;
        this.uriDecoder = uriDecoder;
    }

    @Nullable
    @Override
    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {

        final String part = uri.getSchemeSpecificPart();

        if (TextUtils.isEmpty(part)) {
            return null;
        }

        final DataUri dataUri = uriParser.parse(part);
        if (dataUri == null) {
            return null;
        }

        final byte[] bytes = uriDecoder.decode(dataUri);
        if (bytes == null) {
            return null;
        }

        return new ImageItem(
                dataUri.contentType(),
                new ByteArrayInputStream(bytes),
                null
        );
    }

    @Override
    public void cancel(@NonNull String raw) {
        // no op
    }
}
