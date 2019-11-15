package io.noties.markwon.editor;

import android.text.Editable;
import android.text.Spannable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static io.noties.markwon.editor.MarkwonEditorUtils.extractSpans;

/**
 * Cache for spans that present in user input. These spans are reused between different
 * {@link MarkwonEditor#process(Editable)} and {@link MarkwonEditor#preRender(Editable, MarkwonEditor.PreRenderResultListener)}
 * calls.
 *
 * @see EditHandler#handleMarkdownSpan(PersistedSpans, Editable, String, Object, int, int)
 * @see EditHandler#configurePersistedSpans(Builder)
 * @since 4.2.0
 */
public abstract class PersistedSpans {

    public interface SpanFactory<T> {
        @NonNull
        T create();
    }

    public interface Builder {
        @SuppressWarnings("UnusedReturnValue")
        @NonNull
        <T> Builder persistSpan(@NonNull Class<T> type, @NonNull SpanFactory<T> spanFactory);
    }

    @NonNull
    public abstract <T> T get(@NonNull Class<T> type);

    abstract void removeUnused();


    @NonNull
    static Provider provider() {
        return new Provider();
    }

    static class Provider implements Builder {

        private final Map<Class<?>, SpanFactory> map = new HashMap<>(3);

        @NonNull
        @Override
        public <T> Builder persistSpan(@NonNull Class<T> type, @NonNull SpanFactory<T> spanFactory) {
            if (map.put(type, spanFactory) != null) {
                Log.e("MD-EDITOR", String.format(
                        Locale.ROOT,
                        "Re-declaration of persisted span for '%s'", type.getName()));
            }
            return this;
        }

        @NonNull
        PersistedSpans provide(@NonNull Spannable spannable) {
            return new Impl(spannable, map);
        }
    }

    static class Impl extends PersistedSpans {

        private final Spannable spannable;
        private final Map<Class<?>, SpanFactory> spans;
        private final Map<Class<?>, List<Object>> map;

        Impl(@NonNull Spannable spannable, @NonNull Map<Class<?>, SpanFactory> spans) {
            this.spannable = spannable;
            this.spans = spans;
            this.map = extractSpans(spannable, spans.keySet());
        }

        @NonNull
        @Override
        public <T> T get(@NonNull Class<T> type) {

            final Object span;

            final List<Object> list = map.get(type);
            if (list != null && list.size() > 0) {
                span = list.remove(0);
            } else {
                final SpanFactory spanFactory = spans.get(type);
                if (spanFactory == null) {
                    throw new IllegalStateException("Requested type `" + type.getName() + "` was " +
                            "not registered, use PersistedSpans.Builder#persistSpan method to register");
                }
                span = spanFactory.create();
            }

            //noinspection unchecked
            return (T) span;
        }

        @Override
        void removeUnused() {
            for (List<Object> spans : map.values()) {
                if (spans != null
                        && spans.size() > 0) {
                    for (Object span : spans) {
                        spannable.removeSpan(span);
                    }
                }
            }
        }
    }
}
