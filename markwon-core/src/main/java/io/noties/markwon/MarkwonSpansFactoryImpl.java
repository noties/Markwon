package io.noties.markwon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
        public <N extends Node> Builder setFactory(@NonNull Class<N> node, @Nullable SpanFactory factory) {
            if (factory == null) {
                factories.remove(node);
            } else {
                factories.put(node, factory);
            }
            return this;
        }

        @NonNull
        @Override
        @Deprecated
        public <N extends Node> Builder addFactory(@NonNull Class<N> node, @NonNull SpanFactory factory) {
            return prependFactory(node, factory);
        }

        @NonNull
        @Override
        public <N extends Node> Builder appendFactory(@NonNull Class<N> node, @NonNull SpanFactory factory) {
            final SpanFactory existing = factories.get(node);
            if (existing == null) {
                factories.put(node, factory);
            } else {
                if (existing instanceof CompositeSpanFactory) {
                    ((CompositeSpanFactory) existing).factories.add(0, factory);
                } else {
                    final CompositeSpanFactory compositeSpanFactory =
                            new CompositeSpanFactory(factory, existing);
                    factories.put(node, compositeSpanFactory);
                }
            }
            return this;
        }

        @NonNull
        @Override
        public <N extends Node> Builder prependFactory(@NonNull Class<N> node, @NonNull SpanFactory factory) {
            // if there is no factory registered for this node -> just add it
            final SpanFactory existing = factories.get(node);
            if (existing == null) {
                factories.put(node, factory);
            } else {
                // existing span factory can be of CompositeSpanFactory at this point -> append to it
                if (existing instanceof CompositeSpanFactory) {
                    ((CompositeSpanFactory) existing).factories.add(factory);
                } else {
                    // if it's not composite at this point -> make it
                    final CompositeSpanFactory compositeSpanFactory =
                            new CompositeSpanFactory(existing, factory);
                    factories.put(node, compositeSpanFactory);
                }
            }
            return this;
        }

        @Nullable
        @Override
        public <N extends Node> SpanFactory getFactory(@NonNull Class<N> node) {
            return factories.get(node);
        }

        @NonNull
        @Override
        public <N extends Node> SpanFactory requireFactory(@NonNull Class<N> node) {
            final SpanFactory factory = getFactory(node);
            if (factory == null) {
                throw new NullPointerException(node.getName());
            }
            return factory;
        }

        @NonNull
        @Override
        public MarkwonSpansFactory build() {
            return new MarkwonSpansFactoryImpl(Collections.unmodifiableMap(factories));
        }
    }

    static class CompositeSpanFactory implements SpanFactory {

        final List<SpanFactory> factories;

        CompositeSpanFactory(@NonNull SpanFactory first, @NonNull SpanFactory second) {
            this.factories = new ArrayList<>(3);
            this.factories.add(first);
            this.factories.add(second);
        }

        @Nullable
        @Override
        public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
            // please note that we do not check it factory itself returns an array of spans,
            // as this behaviour is supported now (previously we supported only a single-level array)
            final int length = factories.size();
            final Object[] out = new Object[length];
            for (int i = 0; i < length; i++) {
                out[i] = factories.get(i).getSpans(configuration, props);
            }
            return out;
        }
    }
}
