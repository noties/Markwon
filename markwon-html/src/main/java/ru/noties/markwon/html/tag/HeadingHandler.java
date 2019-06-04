package ru.noties.markwon.html.tag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Heading;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.core.CoreProps;
import ru.noties.markwon.html.HtmlTag;

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
}
