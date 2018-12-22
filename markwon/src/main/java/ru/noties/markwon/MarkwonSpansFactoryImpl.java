package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Node;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 3.0.0
 */
class MarkwonSpansFactoryImpl implements MarkwonSpansFactory {

    private final Map<Class<? extends Node>, SpanFactory> factories;

    private MarkwonSpansFactoryImpl(@NonNull Map<Class<? extends Node>, SpanFactory> factories) {
        this.factories = factories;
    }

    @Nullable
    @Override
    public <N extends Node, F extends SpanFactory> F get(@NonNull Class<N> node) {
        //noinspection unchecked
        return (F) factories.get(node);
    }

    @Nullable
    @Override
    public <N extends Node, F extends SpanFactory> F get(@NonNull N node) {
        return get(node.getClass());
    }

    @NonNull
    @Override
    public <N extends Node, F extends SpanFactory> F require(@NonNull Class<N> node) {
        final F f = get(node);
        if (f == null) {
            throw new NullPointerException();
        }
        return f;
    }

    @NonNull
    @Override
    public <N extends Node, F extends SpanFactory> F require(@NonNull N node) {
        final F f = get(node);
        if (f == null) {
            throw new NullPointerException();
        }
        return f;
    }

    static class BuilderImpl implements Builder {

        private final Map<Class<? extends Node>, SpanFactory> factories =
                new HashMap<>(3);

        @NonNull
        @Override
        public <N extends Node, F extends SpanFactory> Builder setFactory(@NonNull Class<N> node, @NonNull F factory) {
            factories.put(node, factory);
            return this;
        }

        @NonNull
        @Override
        public MarkwonSpansFactory build() {
            return new MarkwonSpansFactoryImpl(Collections.unmodifiableMap(factories));
        }
    }
}
