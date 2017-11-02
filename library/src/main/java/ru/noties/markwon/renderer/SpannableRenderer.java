package ru.noties.markwon.renderer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Node;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;

public class SpannableRenderer {

    @Nullable
    public CharSequence render(@NonNull SpannableConfiguration configuration, @Nullable Node node) {
        final CharSequence out;
        if (node == null) {
            out = null;
        } else {
            final SpannableBuilder builder = new SpannableBuilder();
            node.accept(new SpannableMarkdownVisitor(configuration, builder));
            out = builder.text();
        }
        return out;
    }
}
