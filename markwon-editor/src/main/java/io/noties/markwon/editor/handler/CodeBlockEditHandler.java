package it.niedermann.android.markdown.markwon.handler;

import android.text.Editable;
import android.text.Spanned;

import androidx.annotation.NonNull;

import io.noties.markwon.Markwon;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.core.spans.CodeBlockSpan;
import io.noties.markwon.editor.EditHandler;
import io.noties.markwon.editor.MarkwonEditorUtils;
import io.noties.markwon.editor.PersistedSpans;

public class CodeBlockEditHandler implements EditHandler<CodeBlockSpan> {
    private MarkwonTheme theme;

    @Override
    public void init(@NonNull Markwon markwon) {
        theme = markwon.configuration().theme();
    }

    @Override
    public void configurePersistedSpans(@NonNull PersistedSpans.Builder builder) {
        builder.persistSpan(CodeBlockSpan.class, () -> new CodeBlockSpan(theme));
    }

    @Override
    public void handleMarkdownSpan(@NonNull PersistedSpans persistedSpans, @NonNull Editable editable, @NonNull String input, @NonNull CodeBlockSpan span, int spanStart, int spanTextLength) {
        MarkwonEditorUtils.Match delimited = MarkwonEditorUtils.findDelimited(input, spanStart, "```");
        if (delimited != null) {
            editable.setSpan(
                    persistedSpans.get(markdownSpanType()),
                    delimited.start(),
                    delimited.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE

            );
        }
    }

    @NonNull
    @Override
    public Class<CodeBlockSpan> markdownSpanType() {
        return CodeBlockSpan.class;
    }
}
