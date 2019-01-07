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

    MarkwonSpansFactoryImpl(@NonNull Map<Class<? extends Node>, SpanFactory> factories) {
        this.factories = factories;
    }

    @Nullable
    @Override
    public <N extends Node> SpanFactory get(@NonNull Class<N> node) {
        return factories.get(node);
    }

    @NonNull
    @Override
    public <N extends Node> SpanFactory require(@NonNull Class<N> node) {
        final SpanFactory f = get(node);
        if (f == null) {
            throw new NullPointerException(node.getName());
        }
        return f;
    }

    static class BuilderImpl implements Builder {

        private final Map<Class<? extends Node>, SpanFactory> factories =
                new HashMap<>(3);

        @NonNull
        @Override
        public <N extends Node> Builder setFactory(@NonNull Class<N> node, @NonNull SpanFactory factory) {
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
