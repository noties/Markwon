package io.noties.markwon.html.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Heading;

import java.util.Arrays;
import java.util.Collection;

import io.noties.markwon.core.CoreProps;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;

public class HeadingHandler extends SimpleTagHandler {

    @Nullable
    @Override
    public Object getSpans(
            @NonNull MarkwonConfiguration configuration,
            @NonNull RenderProps renderProps,
            @NonNull HtmlTag tag) {

        final SpanFactory factory = configuration.spansFactory().get(Heading.class);
        if (factory == null) {
            return null;
        }

        int level;
        try {
            level = Integer.parseInt(tag.name().substring(1));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            level = 0;
        }

        if (level < 1 || level > 6) {
            return null;
        }

        CoreProps.HEADING_LEVEL.set(renderProps, level);

        return factory.getSpans(configuration, renderProps);
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Arrays.asList("h1", "h2", "h3", "h4", "h5", "h6");
    }
}
