package io.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.noties.markwon.MarkwonVisitor;

/**
 * @since 2.0.0
 */
public abstract class MarkwonHtmlRenderer {

    /**
     * @since 4.0.0-SNAPSHOT
     */
    @NonNull
    public static MarkwonHtmlRenderer noOp() {
        return new MarkwonHtmlRendererNoOp();
    }

    public abstract void render(
            @NonNull MarkwonVisitor visitor,
            @NonNull MarkwonHtmlParser parser
    );

    @Nullable
    public abstract TagHandler tagHandler(@NonNull String tagName);
}
