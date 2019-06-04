package ru.noties.markwon;

import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import ru.noties.markwon.urlprocessor.UrlProcessor;
import ru.noties.markwon.urlprocessor.UrlProcessorRelativeToAbsolute;

class UrlProcessorInitialReadme implements UrlProcessor {

    private static final String GITHUB_BASE = "https://github.com/noties/Markwon/raw/master/";

    private final UrlProcessorRelativeToAbsolute processor
            = new UrlProcessorRelativeToAbsolute(GITHUB_BASE);

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
