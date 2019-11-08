package io.noties.markwon.editor;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.diff_match_patch.Diff;

class MarkwonEditorImpl extends MarkwonEditor {

    private final Markwon markwon;
    private final Map<Class<?>, EditSpanFactory> spans;
    private final Class<?> punctuationSpanType;

    @Nullable
    private final EditSpanHandler editSpanHandler;

    MarkwonEditorImpl(
            @NonNull Markwon markwon,
            @NonNull Map<Class<?>, EditSpanFactory> spans,
            @NonNull Class<?> punctuationSpanType,
            @Nullable EditSpanHandler editSpanHandler) {
        this.markwon = markwon;
        this.spans = spans;
        this.punctuationSpanType = punctuationSpanType;
        this.editSpanHandler = editSpanHandler;
    }

    @Override
    public void process(@NonNull Editable editable) {

        final String input = editable.toString();

        // NB, we cast to Spannable here without prior checks
        //  if by some occasion Markwon stops returning here a Spannable our tests will catch that
        //  (we need Spannable in order to remove processed spans, so they do not appear multiple times)
        final Spannable renderedMarkdown = (Spannable) markwon.toMarkdown(input);

        final String markdown = renderedMarkdown.toString();

        final EditSpanHandler editSpanHandler = this.editSpanHandler;
        final boolean hasAdditionalSpans = editSpanHandler != null;

        final EditSpanStoreImpl store = new EditSpanStoreImpl(editable, spans);
        try {

            final List<Diff> diffs = diff_match_patch.diff_main(input, markdown);

            int inputLength = 0;
            int markdownLength = 0;

            for (Diff diff : diffs) {

                switch (diff.operation) {

                    case DELETE:

                        final int start = inputLength;
                        inputLength += diff.text.length();
                        editable.setSpan(
                                store.get(punctuationSpanType),
                                start,
                                inputLength,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );

                        if (hasAdditionalSpans) {
                            // obtain spans for a single character of renderedMarkdown
                            //  editable here should return all spans that are contained in specified
                            //  region. Later we match if span starts at current position
                            //  and notify additional span handler about it
                            final Object[] spans = renderedMarkdown.getSpans(markdownLength, markdownLength + 1, Object.class);
                            for (Object span : spans) {
                                if (markdownLength == renderedMarkdown.getSpanStart(span)) {

                                    editSpanHandler.handle(
                                            store,
                                            editable,
                                            input,
                                            span,
                                            start,
                                            renderedMarkdown.getSpanEnd(span) - markdownLength);
                                    // NB, we do not break here in case of SpanFactory
                                    // returns multiple spans for a markdown node, this way
                                    // we will handle all of them

                                    // It is important to remove span after we have processed it
                                    //  as we process them in 2 places: here and in EQUAL
                                    renderedMarkdown.removeSpan(span);
                                }
                            }
                        }
                        break;

                    case INSERT:
                        // no special handling here, but still we must advance the markdownLength
                        markdownLength += diff.text.length();
                        break;

                    case EQUAL:
                        final int length = diff.text.length();
                        final int inputStart = inputLength;
                        final int markdownStart = markdownLength;
                        inputLength += length;
                        markdownLength += length;

                        // it is possible that there are spans for the text that is the same
                        //  for example, if some links were _autolinked_ (text is the same,
                        //  but there is an additional URLSpan)
                        if (hasAdditionalSpans) {
                            final Object[] spans = renderedMarkdown.getSpans(markdownStart, markdownLength, Object.class);
                            for (Object span : spans) {
                                final int spanStart = renderedMarkdown.getSpanStart(span);
                                if (spanStart >= markdownStart) {
                                    final int end = renderedMarkdown.getSpanEnd(span);
                                    if (end <= markdownLength) {

                                        editSpanHandler.handle(
                                                store,
                                                editable,
                                                input,
                                                span,
                                                // shift span to input position (can be different from the text itself)
                                                inputStart + (spanStart - markdownStart),
                                                end - spanStart
                                        );

                                        renderedMarkdown.removeSpan(span);
                                    }
                                }
                            }
                        }
                        break;

                    default:
                        throw new IllegalStateException();
                }
            }

        } finally {
            store.removeUnused();
        }
    }

    @Override
    public void preRender(@NonNull final Editable editable, @NonNull PreRenderResultListener listener) {
        final RecordingSpannableStringBuilder builder = new RecordingSpannableStringBuilder(editable);
        process(builder);
        listener.onPreRenderResult(new PreRenderResult() {
            @NonNull
            @Override
            public Editable resultEditable() {
                // if they are the same, they should be equals then (what about additional spans?? like cursor? it should not interfere....)
                return builder;
            }

            @Override
            public void dispatchTo(@NonNull Editable e) {
                for (Span span : builder.applied) {
                    e.setSpan(span.what, span.start, span.end, span.flags);
                }
                for (Object span : builder.removed) {
                    e.removeSpan(span);
                }
            }
        });
    }

    @NonNull
    static Map<Class<?>, List<Object>> extractSpans(@NonNull Spanned spanned, @NonNull Collection<Class<?>> types) {

        final Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);
        final Map<Class<?>, List<Object>> map = new HashMap<>(3);

        Class<?> type;

        for (Object span : spans) {
            type = span.getClass();
            if (types.contains(type)) {
                List<Object> list = map.get(type);
                if (list == null) {
                    list = new ArrayList<>(3);
                    map.put(type, list);
                }
                list.add(span);
            }
        }

        return map;
    }

    static class EditSpanStoreImpl implements EditSpanStore {

        private final Spannable spannable;
        private final Map<Class<?>, EditSpanFactory> spans;
        private final Map<Class<?>, List<Object>> map;

        EditSpanStoreImpl(@NonNull Spannable spannable, @NonNull Map<Class<?>, EditSpanFactory> spans) {
            this.spannable = spannable;
            this.spans = spans;
            this.map = extractSpans(spannable, spans.keySet());
        }

        @NonNull
        @Override
        public <T> T get(Class<T> type) {

            final Object span;

            final List<Object> list = map.get(type);
            if (list != null && list.size() > 0) {
                span = list.remove(0);
            } else {
                final EditSpanFactory spanFactory = spans.get(type);
                if (spanFactory == null) {
                    throw new IllegalStateException("Requested type `" + type.getName() + "` was " +
                            "not registered, use Builder#includeEditSpan method to register");
                }
                span = spanFactory.create();
            }

            //noinspection unchecked
            return (T) span;
        }

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

    private static class Span {
        final Object what;
        final int start;
        final int end;
        final int flags;

        Span(Object what, int start, int end, int flags) {
            this.what = what;
            this.start = start;
            this.end = end;
            this.flags = flags;
        }
    }

    private static class RecordingSpannableStringBuilder extends SpannableStringBuilder {

        final List<Span> applied = new ArrayList<>(3);
        final List<Object> removed = new ArrayList<>(0);

        RecordingSpannableStringBuilder(CharSequence text) {
            super(text);
        }

        @Override
        public void setSpan(Object what, int start, int end, int flags) {
            super.setSpan(what, start, end, flags);
            applied.add(new Span(what, start, end, flags));
        }

        @Override
        public void removeSpan(Object what) {
            super.removeSpan(what);
            removed.add(what);
        }
    }
}
