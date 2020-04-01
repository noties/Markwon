package io.noties.markwon.html.tag;

import android.text.style.StrikethroughSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;

import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.html.TagHandler;

public class StrikeHandler extends TagHandler {

    // flag to detect if commonmark-java-strikethrough is in classpath, so we use SpanFactory
    // to obtain strikethrough span
    private static final boolean HAS_MARKDOWN_IMPLEMENTATION;

    static {
        boolean hasMarkdownImplementation;
        try {
            // @since 4.3.1 we class Class.forName instead of trying
            //  to access the class by full qualified name (which caused issues with DexGuard)
            Class.forName("org.commonmark.ext.gfm.strikethrough.Strikethrough");
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
