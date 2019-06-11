package io.noties.markwon.html.tag;

import androidx.annotation.NonNull;

import org.commonmark.node.BlockQuote;

import java.util.Collection;
import java.util.Collections;

import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.html.TagHandler;

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

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Collections.singleton("blockquote");
    }
}
