package io.noties.markwon.app;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import io.noties.markwon.image.destination.ImageDestinationProcessor;
import io.noties.markwon.image.destination.ImageDestinationProcessorRelativeToAbsolute;

class ImageDestinationProcessorInitialReadme extends ImageDestinationProcessor {

    private static final String GITHUB_BASE = "https://github.com/noties/Markwon/raw/master/";

    private final ImageDestinationProcessorRelativeToAbsolute processor
            = new ImageDestinationProcessorRelativeToAbsolute(GITHUB_BASE);

    @NonNull
    @Override
    public String process(@NonNull String destination) {
        String out;
        final Uri uri = Uri.parse(destination);
        if (TextUtils.isEmpty(uri.getScheme())) {
            out = processor.process(destination);
        } else {
            out = destination;
        }
        return out;
    }
}
