package io.noties.markwon.sample.editor;

import android.text.Editable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import io.noties.markwon.core.spans.LinkSpan;
import io.noties.markwon.editor.AbstractEditHandler;
import io.noties.markwon.editor.PersistedSpans;

class LinkEditHandler extends AbstractEditHandler<LinkSpan> {

    interface OnClick {
        void onClick(@NonNull View widget, @NonNull String link);
    }

    private final OnClick onClick;

    LinkEditHandler(@NonNull OnClick onClick) {
        this.onClick = onClick;
    }

    @Override
    public void configurePersistedSpans(@NonNull PersistedSpans.Builder builder) {
        builder.persistSpan(EditLinkSpan.class, () -> new EditLinkSpan(onClick));
    }

    @Override
    public void handleMarkdownSpan(
            @NonNull PersistedSpans persistedSpans,
            @NonNull Editable editable,
            @NonNull String input,
            @NonNull LinkSpan span,
            int spanStart,
            int spanTextLength) {

        final EditLinkSpan editLinkSpan = persistedSpans.get(EditLinkSpan.class);
        editLinkSpan.link = span.getLink();

        final int s;
        final int e;

        // markdown link vs. autolink
        if ('[' == input.charAt(spanStart)) {
            s = spanStart + 1;
            e = spanStart + 1 + spanTextLength;
        } else {
            s = spanStart;
            e = spanStart + spanTextLength;
        }

        editable.setSpan(
                editLinkSpan,
                s,
                e,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }

    @NonNull
    @Override
    public Class<LinkSpan> markdownSpanType() {
        return LinkSpan.class;
    }

    static class EditLinkSpan extends ClickableSpan {

        private final OnClick onClick;

        String link;

        EditLinkSpan(@NonNull OnClick onClick) {
            this.onClick = onClick;
        }

        @Override
        public void onClick(@NonNull View widget) {
            if (link != null) {
                onClick.onClick(widget, link);
            }
        }
    }
}
