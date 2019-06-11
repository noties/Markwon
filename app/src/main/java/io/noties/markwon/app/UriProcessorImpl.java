package io.noties.markwon.app;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.List;

class UriProcessorImpl implements UriProcessor {

    private static final String GITHUB = "github.com";

    @Override
    public Uri process(@NonNull final Uri uri) {

        // hm... github, even having a README.md in path will return rendered HTML

        final Uri out;

        if (GITHUB.equals(uri.getAuthority())) {

            final List<String> segments = uri.getPathSegments();
            final int size = segments != null
                    ? segments.size()
                    : 0;

            if (size > 0) {

                // we need to modify the final uri
                final Uri.Builder builder = new Uri.Builder()
                        .scheme(uri.getScheme())
                        .authority(uri.getAuthority())
                        .fragment(uri.getFragment())
                        .query(uri.getQuery());

                for (String segment : segments) {
                    final String part;
                    if ("blob".equals(segment)) {
                        part = "raw";
                    } else {
                        part = segment;
                    }
                    builder.appendPath(part);
                }
                out = builder.build();
            } else {
                out = uri;
            }
        } else {
            out = uri;
        }

        return out;
    }
}
