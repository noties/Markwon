package ru.noties.markwon.html.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Heading;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.core.CoreProps;
import ru.noties.markwon.html.HtmlTag;

public class HeadingHandler extends SimpleTagHandler {

    private final int level;

    public HeadingHandler(int level) {
        this.level = level;
    }

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

        CoreProps.HEADING_LEVEL.set(renderProps, level);

        return factory.getSpans(configuration, renderProps);
    }
}
