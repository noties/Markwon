package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Node;

/**
 * @since 3.0.0
 */
public interface MarkwonSpansFactory {

    @Nullable
    <N extends Node, F extends SpanFactory> F get(@NonNull Class<N> node);

    @Nullable
    <N extends Node, F extends SpanFactory> F get(@NonNull N node);

    @NonNull
    <N extends Node, F extends SpanFactory> F require(@NonNull Class<N> node);

    @NonNull
    <N extends Node, F extends SpanFactory> F require(@NonNull N node);


    interface Builder {

        @NonNull
        <N extends Node, F extends SpanFactory> Builder setFactory(@NonNull Class<N> node, @NonNull F factory);

        @NonNull
        MarkwonSpansFactory build();
    }
}
