package io.noties.markwon.html.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Emphasis;

import java.util.Arrays;
import java.util.Collection;

import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;

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

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Arrays.asList("i", "em", "cite", "dfn");
    }
}
