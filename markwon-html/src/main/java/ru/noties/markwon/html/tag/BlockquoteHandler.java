package ru.noties.markwon.html.tag;

import android.support.annotation.NonNull;

import org.commonmark.node.BlockQuote;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.html.HtmlTag;
import ru.noties.markwon.html.MarkwonHtmlRenderer;
import ru.noties.markwon.html.TagHandler;

public class BlockquoteHandler extends TagHandler {

    @Override
    public void handle(
            @NonNull MarkwonVisitor visitor,
            @NonNull MarkwonHtmlRenderer renderer,
            @NonNull HtmlTag tag) {

        if (tag.isBlock()) {
            visitChildren(visitor, renderer, tag.getAsBlock());
        }

        final MarkwonConfiguration configuration = visitor.configuration();
        final SpanFactory factory = configuration.spansFactory().get(BlockQuote.class);
        if (factory != null) {
            SpannableBuilder.setSpans(
                    visitor.builder(),
                    factory.getSpans(configuration, visitor.renderProps()),
                    tag.start(),
                    tag.end()
            );
        }
    }
}
