package io.noties.markwon.html.tag;

import android.text.style.UnderlineSpan;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Collection;

import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.html.TagHandler;

public class UnderlineHandler extends TagHandler {

    @Override
    public void handle(
            @NonNull MarkwonVisitor visitor,
            @NonNull MarkwonHtmlRenderer renderer,
            @NonNull HtmlTag tag) {

        // as parser doesn't treat U tag as an inline one,
        // thus doesn't allow children, we must visit them first

        if (tag.isBlock()) {
            visitChildren(visitor, renderer, tag.getAsBlock());
        }

        SpannableBuilder.setSpans(
                visitor.builder(),
                new UnderlineSpan(),
                tag.start(),
                tag.end()
        );
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Arrays.asList("u", "ins");
    }
}
