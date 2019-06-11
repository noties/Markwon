package io.noties.markwon.urlprocessor;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Processor that will <em>assume</em> that an URL without scheme points to android assets folder.
 * URL with a scheme will be processed by {@link #processor} (if it is specified) or returned `as-is`.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class UrlProcessorAndroidAssets implements UrlProcessor {


    static final String MOCK = "https://android.asset/";
    static final String BASE = "file:///android_asset/";

    private final UrlProcessorRelativeToAbsolute assetsProcessor
            = new UrlProcessorRelativeToAbsolute(MOCK);

    private final UrlProcessor processor;

    public UrlProcessorAndroidAssets() {
        this(null);
    }

    public UrlProcessorAndroidAssets(@Nullable UrlProcessor parent) {
        this.processor = parent;
    }

    @NonNull
    @Override
    public String process(@NonNull String destination) {
        final String out;
        final Uri uri = Uri.parse(destination);
        if (TextUtils.isEmpty(uri.getScheme())) {
            out = assetsProcessor.process(destination).replace(MOCK, BASE);
        } else {
            if (processor != null) {
                out = processor.process(destination);
            } else {
                out = destination;
            }
        }
        return out;
    }
}
