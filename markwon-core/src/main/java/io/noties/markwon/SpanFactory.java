package io.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @since 3.0.0
 */
public interface SpanFactory {

    @Nullable
    Object getSpans(
            @NonNull MarkwonConfiguration configuration,
            @NonNull RenderProps props);
}
