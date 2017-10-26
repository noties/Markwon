package ru.noties.markwon;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class UrlProcessorAndroidAssets implements UrlProcessor {

    private final UrlProcessorRelativeToAbsolute mAssetsProcessor
            = new UrlProcessorRelativeToAbsolute("file:///android_asset/");

    private final UrlProcessor mProcessor;

    public UrlProcessorAndroidAssets() {
        this(null);
    }

    public UrlProcessorAndroidAssets(@Nullable UrlProcessor parent) {
        mProcessor = parent;
    }

    @NonNull
    @Override
    public String process(@NonNull String destination) {
        final String out;
        final Uri uri = Uri.parse(destination);
        if (TextUtils.isEmpty(uri.getScheme())) {
            out = mAssetsProcessor.process(destination);
        } else {
            if (mProcessor != null) {
                out = mProcessor.process(destination);
            } else {
                out = destination;
            }
        }
        return out;
    }
}
