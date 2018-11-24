package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.html.api.HtmlTag;

public abstract class TagHandler {

    public abstract void handle(
            @NonNull MarkwonConfiguration configuration,
            @NonNull SpannableBuilder builder,
            @NonNull HtmlTag tag
    );

    protected static void visitChildren(
            @NonNull MarkwonConfiguration configuration,
            @NonNull SpannableBuilder builder,
            @NonNull HtmlTag.Block block) {

        TagHandler handler;

        for (HtmlTag.Block child : block.children()) {

            if (!child.isClosed()) {
                continue;
            }

            handler = configuration.htmlRenderer().tagHandler(child.name());
            if (handler != null) {
                handler.handle(configuration, builder, child);
            } else {
                visitChildren(configuration, builder, child);
            }
        }
    }
}
