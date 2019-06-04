package ru.noties.markwon.urlprocessor;

import androidx.annotation.NonNull;

public interface UrlProcessor {
    @NonNull
    String process(@NonNull String destination);
}
