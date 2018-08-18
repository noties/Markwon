package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.html.api.HtmlTag;

public class ListHandler implements TagHandler {

    @Override
    public void handle(
            @NonNull SpannableConfiguration configuration,
            @NonNull SpannableBuilder builder,
            @NonNull HtmlTag tag) {

        if (!tag.isBlock()) {
            return;
        }

        final HtmlTag.Block block = tag.getAsBlock();
        final boolean ol = "ol".equals(block.name());
        final boolean ul = "ul".equals(block.name());

        if (!ol && !ul) {
            return;
        }

        int number = 1;
        final int bulletLevel = currentBulletListLevel(block);

        Object spans;

        for (HtmlTag.Block child : block.children()) {

            visitChildren(configuration, builder, child);

            if ("li".equals(child.name())) {
                // insert list item here
                if (ol) {
                    spans = configuration.factory().orderedListItem(
                            configuration.theme(),
                            number++
                    );
                } else {
                    spans = configuration.factory().bulletListItem(
                            configuration.theme(),
                            bulletLevel
                    );
                }
                SpannableBuilder.setSpans(builder, spans, child.start(), child.end());
            }
        }
    }

    private void visitChildren(
            @NonNull SpannableConfiguration configuration,
            @NonNull SpannableBuilder builder,
            @NonNull HtmlTag.Block block) {

        TagHandler handler;

        for (HtmlTag.Block child : block.children()) {
            handler = configuration.htmlRenderer().tagHandler(child.name());
            if (handler != null) {
                handler.handle(configuration, builder, child);
            } else {
                visitChildren(configuration, builder, child);
            }
        }
    }

    private static int currentBulletListLevel(@NonNull HtmlTag.Block block) {
        int level = 0;
        while ((block = block.parent()) != null) {
            if ("ul".equals(block.name())
                    || "ol".equals(block.name())) {
                level += 1;
            }
        }
        return level;
    }
}
