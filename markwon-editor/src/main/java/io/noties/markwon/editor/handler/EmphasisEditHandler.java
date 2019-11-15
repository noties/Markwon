package io.noties.markwon.editor.handler;

import android.text.Editable;
import android.text.Spanned;

import androidx.annotation.NonNull;

import io.noties.markwon.core.spans.EmphasisSpan;
import io.noties.markwon.editor.AbstractEditHandler;
import io.noties.markwon.editor.MarkwonEditorUtils;
import io.noties.markwon.editor.PersistedSpans;

/**
 * @since 4.2.0
 */
public class EmphasisEditHandler extends AbstractEditHandler<EmphasisSpan> {

    @Override
    public void configurePersistedSpans(@NonNull PersistedSpans.Builder builder) {
        builder.persistSpan(EmphasisSpan.class, new PersistedSpans.SpanFactory<EmphasisSpan>() {
            @NonNull
            @Override
            public EmphasisSpan create() {
                return new EmphasisSpan();
            }
        });
    }

    @Override
    public void handleMarkdownSpan(
            @NonNull PersistedSpans persistedSpans,
            @NonNull Editable editable,
            @NonNull String input,
            @NonNull EmphasisSpan span,
            int spanStart,
            int spanTextLength) {
        final MarkwonEditorUtils.Match match =
                MarkwonEditorUtils.findDelimited(input, spanStart, "*", "_");
        if (match != null) {
            editable.setSpan(
                    persistedSpans.get(EmphasisSpan.class),
                    match.start(),
                    match.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
    }

    @NonNull
    @Override
    public Class<EmphasisSpan> markdownSpanType() {
        return EmphasisSpan.class;
    }
}
