package ru.noties.markwon.html;

import android.support.annotation.NonNull;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.SpannableBuilder;

public abstract class TagHandler {

    public abstract void handle(
            @NonNull MarkwonConfiguration configuration,
            @NonNull MarkwonHtmlRenderer renderer,
            @NonNull SpannableBuilder builder,
            @NonNull HtmlTag tag
    );

    protected static void visitChildren(
            @NonNull MarkwonConfiguration configuration,
            @NonNull MarkwonHtmlRenderer renderer,
            @NonNull SpannableBuilder builder,
            @NonNull HtmlTag.Block block) {

        TagHandler handler;

        for (HtmlTag.Block child : block.children()) {

            if (!child.isClosed()) {
                continue;
            }

            handler = renderer.tagHandler(child.name());
            if (handler != null) {
                handler.handle(configuration, renderer, builder, child);
            } else {
                visitChildren(configuration, renderer, builder, child);
            }
        }
    }
}
