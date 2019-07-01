package io.noties.markwon.html;

import androidx.annotation.NonNull;

import java.util.Collection;

import io.noties.markwon.MarkwonVisitor;

public abstract class TagHandler {

    public abstract void handle(
            @NonNull MarkwonVisitor visitor,
            @NonNull MarkwonHtmlRenderer renderer,
            @NonNull HtmlTag tag
    );

    /**
     * @since 4.0.0
     */
    @NonNull
    public abstract Collection<String> supportedTags();


    protected static void visitChildren(
            @NonNull MarkwonVisitor visitor,
            @NonNull MarkwonHtmlRenderer renderer,
            @NonNull HtmlTag.Block block) {

        TagHandler handler;

        for (HtmlTag.Block child : block.children()) {

            if (!child.isClosed()) {
                continue;
            }

            handler = renderer.tagHandler(child.name());
            if (handler != null) {
                handler.handle(visitor, renderer, child);
            } else {
                visitChildren(visitor, renderer, child);
            }
        }
    }
}
