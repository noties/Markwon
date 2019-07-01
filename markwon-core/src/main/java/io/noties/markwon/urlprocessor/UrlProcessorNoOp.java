package io.noties.markwon.urlprocessor;

import androidx.annotation.NonNull;

public class UrlProcessorNoOp implements UrlProcessor {
    @NonNull
    @Override
    public String process(@NonNull String destination) {
        return destination;
    }
}
