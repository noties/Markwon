package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Node;

/**
 * Class that controls what spans are used for certain Nodes.
 *
 * @see SpanFactory
 * @since 3.0.0
 */
public interface MarkwonSpansFactory {

    /**
     * Returns registered {@link SpanFactory} or <code>null</code> if a factory for this node type
     * is not registered. There is {@link #require(Class)} method that will throw an exception
     * if required {@link SpanFactory} is not registered, thus making return type <code>non-null</code>
     *
     * @param node type of the node
     * @return registered {@link SpanFactory} or null if it\'s not registered
     * @see #require(Class)
     */
    @Nullable
    <N extends Node> SpanFactory get(@NonNull Class<N> node);

    @NonNull
    <N extends Node> SpanFactory require(@NonNull Class<N> node);


    interface Builder {

        @NonNull
        <N extends Node> Builder setFactory(@NonNull Class<N> node, @Nullable SpanFactory factory);

        /**
         * Helper method to add a {@link SpanFactory} for a Node. This method will merge existing
         * {@link SpanFactory} with the specified one.
         *
         * @since 3.0.1-SNAPSHOT
         */
        @NonNull
        <N extends Node> Builder addFactory(@NonNull Class<N> node, @NonNull SpanFactory factory);

        /**
         * Can be useful when <em>enhancing</em> an already defined SpanFactory with another one.
         */
        @Nullable
        <N extends Node> SpanFactory getFactory(@NonNull Class<N> node);

        /**
         * To obtain current {@link SpanFactory} associated with specified node. Can be used
         * when SpanFactory must be present for node. If it\'s not added/registered a runtime
         * exception will be thrown
         *
         * @see #getFactory(Class)
         * @since 3.0.1-SNAPSHOT
         */
        @NonNull
        <N extends Node> SpanFactory requireFactory(@NonNull Class<N> node);

        @NonNull
        MarkwonSpansFactory build();
    }
}
