package ru.noties.markwon.renderer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

import org.commonmark.node.Node;

// please note that this class does not implement Renderer in order to return CharSequence (instead of String)
public class SpannableRenderer {

    // todo
    // * LinkDrawableSpan, that draws link whilst image is still loading (it must be clickable...)
    // * Common interface for images (in markdown & inline-html)
    // * util method to properly copy markdown (with lists/links, etc)
    // * util to apply empty line height
    // * transform relative urls to absolute ones...

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
