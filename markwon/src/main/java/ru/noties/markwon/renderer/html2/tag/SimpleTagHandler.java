package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.html.api.HtmlTag;

public abstract class SimpleTagHandler extends TagHandler {

    @Nullable
    public abstract Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull HtmlTag tag);

    @Override
    public void handle(@NonNull MarkwonConfiguration configuration, @NonNull SpannableBuilder builder, @NonNull HtmlTag tag) {
        final Object spans = getSpans(configuration, tag);
        if (spans != null) {
            SpannableBuilder.setSpans(builder, spans, tag.start(), tag.end());
        }
    }
}
