package ru.noties.markwon;

import android.net.Uri;
import android.support.annotation.NonNull;

public interface UrlProvider {
    String provide(@NonNull Uri uri);
}
