package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.html.api.HtmlTag;

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
                configuration.factory().strikethrough(),
                tag.start(),
                tag.end()
        );
    }
}
