package ru.noties.markwon.image.data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayInputStream;

import ru.noties.markwon.image.ImageItem;
import ru.noties.markwon.image.SchemeHandler;

/**
 * @since 2.0.0
 */
public class DataUriSchemeHandler extends SchemeHandler {

    public static final String SCHEME = "data";

    @NonNull
    public static DataUriSchemeHandler create() {
        return new DataUriSchemeHandler(DataUriParser.create(), DataUriDecoder.create());
    }

    private static final String START = "data:";

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

        if (!raw.startsWith(START)) {
            return null;
        }

        String part = raw.substring(START.length());

        // this part is added to support `data://` with which this functionality was released
        if (part.startsWith("//")) {
            part = part.substring(2);
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
                new ByteArrayInputStream(bytes)
        );
    }
}
