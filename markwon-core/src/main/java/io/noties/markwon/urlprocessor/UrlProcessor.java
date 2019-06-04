package io.noties.markwon.urlprocessor;

import android.support.annotation.NonNull;

public interface UrlProcessor {
    @NonNull
    String process(@NonNull String destination);
}
