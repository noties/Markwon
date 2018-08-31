package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;
import android.text.Spannable;

import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.html.api.HtmlTag;

public abstract class TagHandler {

    public abstract void handle(
            @NonNull SpannableConfiguration configuration,
            @NonNull Spannable spannable,
            @NonNull HtmlTag tag
    );

    protected static void visitChildren(
            @NonNull SpannableConfiguration configuration,
            @NonNull Spannable spannable,
            @NonNull HtmlTag.Block block) {

        TagHandler handler;

        for (HtmlTag.Block child : block.children()) {

            if (!child.isClosed()) {
                continue;
            }

            handler = configuration.htmlRenderer().tagHandler(child.name());
            if (handler != null) {
                handler.handle(configuration, spannable, child);
            } else {
                visitChildren(configuration, spannable, child);
            }
        }
    }
}
