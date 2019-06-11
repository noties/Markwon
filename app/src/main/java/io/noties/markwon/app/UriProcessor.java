package io.noties.markwon.app;

import android.net.Uri;

import androidx.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public interface UriProcessor {
    Uri process(@NonNull Uri uri);
}
