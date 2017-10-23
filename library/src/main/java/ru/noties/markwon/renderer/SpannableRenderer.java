package ru.noties.markwon.renderer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

import org.commonmark.node.Node;

import ru.noties.markwon.SpannableConfiguration;

public class SpannableRenderer {

    @Nullable
    public CharSequence render(@NonNull SpannableConfiguration configuration, @Nullable Node node) {
        final CharSequence out;
        if (node == null) {
            out = null;
        } else {
            final SpannableStringBuilder builder = new SpannableStringBuilder();
            node.accept(new SpannableMarkdownVisitor(configuration, builder));
            out = builder;
        }
        return out;
    }
}
