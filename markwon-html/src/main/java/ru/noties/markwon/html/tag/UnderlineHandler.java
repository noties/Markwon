package ru.noties.markwon.html.tag;

import android.support.annotation.NonNull;
import android.text.style.UnderlineSpan;

import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.html.HtmlTag;
import ru.noties.markwon.html.MarkwonHtmlRenderer;
import ru.noties.markwon.html.TagHandler;

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
}
