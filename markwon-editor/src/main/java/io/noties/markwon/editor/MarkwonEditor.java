package io.noties.markwon.editor;

import android.text.Editable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import io.noties.markwon.Markwon;

// todo: if multiple spans are used via factory... only the first one is delivered to edit-span-handler
//  does it though? yeah... only the first one and then break... deliver all?

// todo: how to reuse existing spanFactories? to obtain a value they require render-props....
//  maybe.. mock them? plus, spanFactory can return multiple spans

// todo: we can actually create punctuation span with reasonable defaults to be used by default

/**
 * @since 4.2.0-SNAPSHOT
 */
public abstract class MarkwonEditor {

    public interface SpanStore {

        /**
         * If a span of specified type was not registered with {@link Builder#includeEditSpan(Class, SpanFactory)}
         * then an exception is raised.
         *
         * @param type of a span to obtain
         * @return cached or newly created span
         */
        @NonNull
        <T> T get(Class<T> type);
    }

    public interface SpanFactory<T> {
        @NonNull
        T create();
    }

    public interface EditSpanHandler<T> {
        void handle(
                @NonNull SpanStore store,
                @NonNull Editable editable,
                @NonNull String input,
                @NonNull T span,
                int spanStart,
                int spanTextLength);
    }

    public interface PreRenderResult {

        // With which pre-render method was called as input
        @NonNull
        Editable resultEditable();

        void dispatchTo(@NonNull Editable editable);
    }

    public interface PreRenderResultListener {
        void onPreRenderResult(@NonNull PreRenderResult result);
    }

    @NonNull
    public static Builder builder(@NonNull Markwon markwon) {
        return new Builder(markwon);
    }

    /**
     * Synchronous method that processes supplied Editable in-place. If you wish to move this job
     * to another thread consider using {@link #preRender(Editable, PreRenderResultListener)}
     *
     * @param editable to process
     * @see #preRender(Editable, PreRenderResultListener)
     */
    public abstract void process(@NonNull Editable editable);

    /**
     * Please note that currently only `setSpan` and `removeSpan` actions will be recorded (and thus dispatched).
     * make sure you use only these methods in your {@link EditSpanHandler}, or implement the required
     * functionality in some other way.
     *
     * @param editable          to process and pre-render
     * @param preRenderListener listener to be notified when pre-render result will be ready
     * @see #process(Editable)
     */
    public abstract void preRender(@NonNull Editable editable, @NonNull PreRenderResultListener preRenderListener);

    public static class Builder {

        private final Markwon markwon;
        private final EditSpanHandlerBuilder editSpanHandlerBuilder = new EditSpanHandlerBuilder();

        private Class<?> punctuationSpanType;
        private Map<Class<?>, SpanFactory> spans = new HashMap<>(3);

        Builder(@NonNull Markwon markwon) {
            this.markwon = markwon;
        }

        /**
         * The only required argument which will make {@link MarkwonEditor} apply specified span only
         * to markdown punctuation
         *
         * @param type    of the span
         * @param factory to create a new instance of the span
         */
        @NonNull
        public <T> Builder withPunctuationSpan(@NonNull Class<T> type, @NonNull SpanFactory<T> factory) {
            this.punctuationSpanType = type;
            this.spans.put(type, factory);
            return this;
        }

        /**
         * Include specific span that will be used in highlighting. It is important to understand
         * that it is not the span that is used by Markwon, but instead your own span that you
         * apply in a custom {@link EditSpanHandler} specified by {@link #withEditSpanHandlerFor(Class, EditSpanHandler)}.
         * You can apply a Markwon bundled span (or any other) but it must be still explicitly
         * included by this method.
         * <p>
         * The span will be exposed via {@link SpanStore} in your custom {@link EditSpanHandler}.
         * If you do not use a custom {@link EditSpanHandler} you do not need to specify any span here.
         *
         * @param type    of a span to include
         * @param factory to create a new instance of a span if one is missing from processed Editable
         */
        @NonNull
        public <T> Builder includeEditSpan(
                @NonNull Class<T> type,
                @NonNull SpanFactory<T> factory) {
            this.spans.put(type, factory);
            return this;
        }

        @NonNull
        public <T> Builder withEditSpanHandlerFor(@NonNull Class<T> type, @NonNull EditSpanHandler<T> editSpanHandler) {
            this.editSpanHandlerBuilder.include(type, editSpanHandler);
            return this;
        }

        @NonNull
        public MarkwonEditor build() {

            final Class<?> punctuationSpanType = this.punctuationSpanType;
            if (punctuationSpanType == null) {
                throw new IllegalStateException("Punctuation span type is required, " +
                        "add with Builder#withPunctuationSpan method");
            }

            return new MarkwonEditorImpl(
                    markwon,
                    spans,
                    punctuationSpanType,
                    editSpanHandlerBuilder.build());
        }
    }
}
