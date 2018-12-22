package ru.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.MarkwonVisitor;

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
