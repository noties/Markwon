package ru.noties.markwon.html.tag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import org.commonmark.node.Link;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.core.CoreProps;
import ru.noties.markwon.html.HtmlTag;

public class LinkHandler extends SimpleTagHandler {
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps renderProps, @NonNull HtmlTag tag) {
        final String destination = tag.attributes().get("href");
        if (!TextUtils.isEmpty(destination)) {
            final SpanFactory spanFactory = configuration.spansFactory().get(Link.class);
            if (spanFactory != null) {

                CoreProps.LINK_DESTINATION.set(
                        renderProps,
                        configuration.urlProcessor().process(destination));

                return spanFactory.getSpans(configuration, renderProps);
            }
        }
        return null;
    }
}
