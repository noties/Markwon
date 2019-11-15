package io.noties.markwon.editor.handler;

import android.text.Editable;
import android.text.Spanned;

import androidx.annotation.NonNull;

import io.noties.markwon.core.spans.StrongEmphasisSpan;
import io.noties.markwon.editor.AbstractEditHandler;
import io.noties.markwon.editor.MarkwonEditorUtils;
import io.noties.markwon.editor.PersistedSpans;

/**
 * @since 4.2.0
 */
public class StrongEmphasisEditHandler extends AbstractEditHandler<StrongEmphasisSpan> {

    @NonNull
    public static StrongEmphasisEditHandler create() {
        return new StrongEmphasisEditHandler();
    }

    @Override
    public void configurePersistedSpans(@NonNull PersistedSpans.Builder builder) {
        builder.persistSpan(StrongEmphasisSpan.class, new PersistedSpans.SpanFactory<StrongEmphasisSpan>() {
            @NonNull
            @Override
            public StrongEmphasisSpan create() {
                return new StrongEmphasisSpan();
            }
        });
    }

    @Override
    public void handleMarkdownSpan(
            @NonNull PersistedSpans persistedSpans,
            @NonNull Editable editable,
            @NonNull String input,
            @NonNull StrongEmphasisSpan span,
            int spanStart,
            int spanTextLength) {
        // inline spans can delimit other inline spans,
        //  for example: `**_~~hey~~_**`, this is why we must additionally find delimiter used
        //  and its actual start/end positions
        final MarkwonEditorUtils.Match match =
                MarkwonEditorUtils.findDelimited(input, spanStart, "**", "__");
        if (match != null) {
            editable.setSpan(
                    persistedSpans.get(StrongEmphasisSpan.class),
                    match.start(),
                    match.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
    }

    @NonNull
    @Override
    public Class<StrongEmphasisSpan> markdownSpanType() {
        return StrongEmphasisSpan.class;
    }
}
