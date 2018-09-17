package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.html.api.HtmlTag;

public class BlockquoteHandler extends TagHandler {

    @Override
    public void handle(
            @NonNull SpannableConfiguration configuration,
            @NonNull SpannableBuilder builder,
            @NonNull HtmlTag tag) {

        if (tag.isBlock()) {
            visitChildren(configuration, builder, tag.getAsBlock());
        }

        SpannableBuilder.setSpans(
                builder,
                configuration.factory().blockQuote(configuration.theme()),
                tag.start(),
                tag.end()
        );
    }
}
