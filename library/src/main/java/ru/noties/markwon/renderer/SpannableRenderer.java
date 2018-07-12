package ru.noties.markwon.renderer;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;

import org.commonmark.node.Node;

import ru.noties.markwon.ReverseSpannableStringBuilder;
import ru.noties.markwon.SpannableConfiguration;

public class SpannableRenderer {

    @NonNull
    public CharSequence render(@NonNull SpannableConfiguration configuration, @NonNull Node node) {
        final SpannableStringBuilder builder = new ReverseSpannableStringBuilder();
        node.accept(new SpannableMarkdownVisitor(configuration, builder));
        return builder;
    }
}
