package ru.noties.markwon.html.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Emphasis;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.html.HtmlTag;

public class EmphasisHandler extends SimpleTagHandler {
    @Nullable
    @Override
    public Object getSpans(
            @NonNull MarkwonConfiguration configuration,
            @NonNull RenderProps renderProps,
            @NonNull HtmlTag tag) {
        final SpanFactory spanFactory = configuration.spansFactory().get(Emphasis.class);
        if (spanFactory == null) {
            return null;
        }
        return spanFactory.getSpans(configuration, renderProps);
    }
}
