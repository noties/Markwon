package io.noties.markwon.editor;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.diff_match_patch.Diff;

class MarkwonEditorImpl extends MarkwonEditor {

    private final Markwon markwon;
    private final PersistedSpans.Provider persistedSpansProvider;
    private final Class<?> punctuationSpanType;

    @Nullable
    private final SpansHandler spansHandler;

    MarkwonEditorImpl(
            @NonNull Markwon markwon,
            @NonNull PersistedSpans.Provider persistedSpansProvider,
            @NonNull Class<?> punctuationSpanType,
            @Nullable SpansHandler spansHandler) {
        this.markwon = markwon;
        this.persistedSpansProvider = persistedSpansProvider;
        this.punctuationSpanType = punctuationSpanType;
        this.spansHandler = spansHandler;
    }

    @Override
    public void process(@NonNull Editable editable) {

        final String input = editable.toString();

        // NB, we cast to Spannable here without prior checks
        //  if by some occasion Markwon stops returning here a Spannable our tests will catch that
        //  (we need Spannable in order to remove processed spans, so they do not appear multiple times)
        final Spannable renderedMarkdown = (Spannable) markwon.toMarkdown(input);

        final String markdown = renderedMarkdown.toString();

        final SpansHandler spansHandler = this.spansHandler;
        final boolean hasAdditionalSpans = spansHandler != null;

        final PersistedSpans persistedSpans = persistedSpansProvider.provide(editable);
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
                                persistedSpans.get(punctuationSpanType),
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

                                    spansHandler.handle(
                                            persistedSpans,
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

                                        spansHandler.handle(
                                                persistedSpans,
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
            persistedSpans.removeUnused();
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
