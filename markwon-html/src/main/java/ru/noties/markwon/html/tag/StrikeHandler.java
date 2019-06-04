package ru.noties.markwon.html.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.StrikethroughSpan;

import java.util.Arrays;
import java.util.Collection;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.html.HtmlTag;
import ru.noties.markwon.html.MarkwonHtmlRenderer;
import ru.noties.markwon.html.TagHandler;

public class StrikeHandler extends TagHandler {

    // flag to detect if commonmark-java-strikethrough is in classpath, so we use SpanFactory
    // to obtain strikethrough span
    private static final boolean HAS_MARKDOWN_IMPLEMENTATION;

    static {
        boolean hasMarkdownImplementation;
        try {
            org.commonmark.ext.gfm.strikethrough.Strikethrough.class.getName();
            hasMarkdownImplementation = true;
        } catch (Throwable t) {
            hasMarkdownImplementation = false;
        }
        HAS_MARKDOWN_IMPLEMENTATION = hasMarkdownImplementation;
    }

    @Override
    public void handle(
            @NonNull MarkwonVisitor visitor,
            @NonNull MarkwonHtmlRenderer renderer,
            @NonNull HtmlTag tag) {

        if (tag.isBlock()) {
            visitChildren(visitor, renderer, tag.getAsBlock());
        }

        SpannableBuilder.setSpans(
                visitor.builder(),
                HAS_MARKDOWN_IMPLEMENTATION ? getMarkdownSpans(visitor) : new StrikethroughSpan(),
                tag.start(),
                tag.end()
        );
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Arrays.asList("s", "del");
    }

    @Nullable
    private static Object getMarkdownSpans(@NonNull MarkwonVisitor visitor) {
        final MarkwonConfiguration configuration = visitor.configuration();
        final SpanFactory spanFactory = configuration.spansFactory()
                .get(org.commonmark.ext.gfm.strikethrough.Strikethrough.class);
        if (spanFactory == null) {
            return null;
        }
        return spanFactory.getSpans(configuration, visitor.renderProps());
    }
}
