package io.noties.markwon.html.tag;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Link;

import java.util.Collection;
import java.util.Collections;

import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.html.HtmlTag;

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
                        destination
                );

                return spanFactory.getSpans(configuration, renderProps);
            }
        }
        return null;
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Collections.singleton("a");
    }
}
