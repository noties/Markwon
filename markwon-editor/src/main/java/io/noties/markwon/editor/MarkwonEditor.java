package io.noties.markwon.editor;

import android.text.Editable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import io.noties.markwon.Markwon;

/**
 * @see #builder(Markwon)
 * @see #create(Markwon)
 * @see #process(Editable)
 * @see #preRender(Editable, PreRenderResultListener)
 * @since 4.2.0-SNAPSHOT
 */
public abstract class MarkwonEditor {

    /**
     * Represents cache of spans that are used during highlight
     */
    public interface EditSpanStore {

        /**
         * If a span of specified type was not registered with {@link Builder#includeEditSpan(Class, EditSpanFactory)}
         * then an exception is raised.
         *
         * @param type of a span to obtain
         * @return cached or newly created span
         */
        @NonNull
        <T> T get(Class<T> type);
    }

    public interface EditSpanFactory<T> {
        @NonNull
        T create();
    }

    /**
     * Interface to handle _original_ span that is present in rendered markdown. Can be useful
     * to add specific spans for EditText (for example, make text bold to better indicate
     * strong emphasis used in markdown input).
     *
     * @see Builder#withEditSpanHandler(EditSpanHandler)
     * @see EditSpanHandlerBuilder
     */
    public interface EditSpanHandler {
        void handle(
                @NonNull EditSpanStore store,
                @NonNull Editable editable,
                @NonNull String input,
                @NonNull Object span,
                int spanStart,
                int spanTextLength);
    }

    public interface PreRenderResult {

        /**
         * @return Editable instance for which result was calculated. This must not be
         * actual Editable of EditText
         */
        @NonNull
        Editable resultEditable();

        /**
         * Dispatch pre-rendering result to EditText
         *
         * @param editable to dispatch result to
         */
        void dispatchTo(@NonNull Editable editable);
    }

    /**
     * @see #preRender(Editable, PreRenderResultListener)
     */
    public interface PreRenderResultListener {
        void onPreRenderResult(@NonNull PreRenderResult result);
    }

    /**
     * Creates default instance of {@link MarkwonEditor}. By default it will handle only
     * punctuation spans (highlight markdown punctuation and nothing more).
     *
     * @see #builder(Markwon)
     */
    @NonNull
    public static MarkwonEditor create(@NonNull Markwon markwon) {
        return builder(markwon).build();
    }

    /**
     * @see #create(Markwon)
     * @see Builder
     */
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
     * Pre-render highlight result. Can be useful to create highlight information on a different
     * thread.
     * <p>
     * Please note that currently only `setSpan` and `removeSpan` actions will be recorded (and thus dispatched).
     * Make sure you use only these methods in your {@link EditSpanHandler}, or implement the required
     * functionality some other way.
     *
     * @param editable          to process and pre-render
     * @param preRenderListener listener to be notified when pre-render result will be ready
     * @see #process(Editable)
     */
    public abstract void preRender(@NonNull Editable editable, @NonNull PreRenderResultListener preRenderListener);


    public static class Builder {

        private final Markwon markwon;

        private Class<?> punctuationSpanType;
        private Map<Class<?>, EditSpanFactory> spans = new HashMap<>(3);
        private EditSpanHandler editSpanHandler;

        Builder(@NonNull Markwon markwon) {
            this.markwon = markwon;
        }

        /**
         * Specify which punctuation span will be used.
         *
         * @param type    of the span
         * @param factory to create a new instance of the span
         */
        @NonNull
        public <T> Builder withPunctuationSpan(@NonNull Class<T> type, @NonNull EditSpanFactory<T> factory) {
            this.punctuationSpanType = type;
            this.spans.put(type, factory);
            return this;
        }

        /**
         * Include additional span handling that is used in highlighting. It is important to understand
         * that it is not the span that is used by Markwon, but instead your own span that you
         * apply in a custom {@link EditSpanHandler} specified by {@link #withEditSpanHandler(EditSpanHandler)}.
         * You can apply a Markwon bundled span (or any other) but it must be still explicitly
         * included by this method.
         * <p>
         * The span will be exposed via {@link EditSpanStore} in your custom {@link EditSpanHandler}.
         * If you do not use a custom {@link EditSpanHandler} you do not need to specify any span here.
         *
         * @param type    of a span to include
         * @param factory to create a new instance of a span if one is missing from processed Editable
         */
        @NonNull
        public <T> Builder includeEditSpan(
                @NonNull Class<T> type,
                @NonNull EditSpanFactory<T> factory) {
            this.spans.put(type, factory);
            return this;
        }

        /**
         * Additional handling of markdown spans.
         *
         * @param editSpanHandler handler for additional highlight spans
         * @see EditSpanHandler
         * @see EditSpanHandlerBuilder
         */
        @NonNull
        public Builder withEditSpanHandler(@Nullable EditSpanHandler editSpanHandler) {
            this.editSpanHandler = editSpanHandler;
            return this;
        }

        @NonNull
        public MarkwonEditor build() {

            Class<?> punctuationSpanType = this.punctuationSpanType;
            if (punctuationSpanType == null) {
                withPunctuationSpan(PunctuationSpan.class, new EditSpanFactory<PunctuationSpan>() {
                    @NonNull
                    @Override
                    public PunctuationSpan create() {
                        return new PunctuationSpan();
                    }
                });
                punctuationSpanType = this.punctuationSpanType;
            }

            // if we have no editSpanHandler, but spans are registered -> throw an error
            if (spans.size() > 1 && editSpanHandler == null) {
                throw new IllegalStateException("There is no need to include edit spans " +
                        "when you do not use custom EditSpanHandler");
            }

            return new MarkwonEditorImpl(
                    markwon,
                    spans,
                    punctuationSpanType,
                    editSpanHandler);
        }
    }
}
