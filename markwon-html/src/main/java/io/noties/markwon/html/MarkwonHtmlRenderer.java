package io.noties.markwon.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.MarkwonVisitor;

/**
 * @since 2.0.0
 */
public abstract class MarkwonHtmlRenderer {

    public abstract void render(
            @NonNull MarkwonVisitor visitor,
            @NonNull MarkwonHtmlParser parser
    );

    @Nullable
    public abstract TagHandler tagHandler(@NonNull String tagName);
}
