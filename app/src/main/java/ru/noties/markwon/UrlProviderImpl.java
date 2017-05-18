package ru.noties.markwon;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

class UrlProviderImpl implements UrlProvider {

    private static final String GITHUB = "github.com";

    @Override
    public String provide(@NonNull Uri uri) {

        // hm... github, even having a README.md in path will return rendered HTML

        if (GITHUB.equals(uri.getAuthority())) {
            final List<String> segments = uri.getPathSegments();
            if (segments != null
                    && segments.contains("blob")) {
                // we need to modify the final uri
                final Uri.Builder builder = new Uri.Builder()
                        .scheme(uri.getScheme())
                        .authority(uri.getAuthority())
                        .fragment(uri.getFragment())
                        .query(uri.getQuery());
                for (String segment: segments) {
                    final String part;
                    if ("blob".equals(segment)) {
                        part = "raw";
                    } else {
                        part = segment;
                    }
                    builder.appendPath(part);
                }
                uri = builder.build();
            }
        }

        return uri.toString();
    }
}
