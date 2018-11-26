package ru.noties.markwon.html.impl.tag;

import android.support.annotation.NonNull;
import android.text.style.StrikethroughSpan;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.html.HtmlTag;
import ru.noties.markwon.html.TagHandler;

public class StrikeHandler extends TagHandler {

    @Override
    public void handle(
            @NonNull MarkwonConfiguration configuration,
            @NonNull SpannableBuilder builder,
            @NonNull HtmlTag tag) {

        if (tag.isBlock()) {
            visitChildren(configuration, builder, tag.getAsBlock());
        }

        SpannableBuilder.setSpans(
                builder,
                new StrikethroughSpan(),
                tag.start(),
                tag.end()
        );
    }
}
