package io.noties.markwon.html;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import io.noties.markwon.MarkwonVisitor;

/**
 * @since 4.0.0
 */
public class TagHandlerNoOp extends TagHandler {

    @NonNull
    public static TagHandlerNoOp create(@NonNull String tag) {
        return new TagHandlerNoOp(Collections.singleton(tag));
    }

    @NonNull
    public static TagHandlerNoOp create(@NonNull String... tags) {
        return new TagHandlerNoOp(Arrays.asList(tags));
    }

    private final Collection<String> tags;

    @SuppressWarnings("WeakerAccess")
    TagHandlerNoOp(Collection<String> tags) {
        this.tags = tags;
    }

    @Override
    public void handle(@NonNull MarkwonVisitor visitor, @NonNull MarkwonHtmlRenderer renderer, @NonNull HtmlTag tag) {
        // no op
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return tags;
    }
}
