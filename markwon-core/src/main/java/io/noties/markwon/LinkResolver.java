package io.noties.markwon;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * @see LinkResolverDef
 * @see MarkwonConfiguration.Builder#linkResolver(LinkResolver)
 * @since 4.0.0
 */
public interface LinkResolver {
    void resolve(@NonNull View view, @NonNull String link);
}
