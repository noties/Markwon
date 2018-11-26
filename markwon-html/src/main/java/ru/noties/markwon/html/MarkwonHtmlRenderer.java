package ru.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.SpannableBuilder;

/**
 * @since 2.0.0
 */
public abstract class MarkwonHtmlRenderer {

    /**
     * @since 3.0.0
     */
    @NonNull
    public static MarkwonHtmlRenderer noOp() {
        return new MarkwonHtmlRendererNoOp();
    }

    public abstract void render(
            @NonNull MarkwonConfiguration configuration,
            @NonNull SpannableBuilder builder,
            @NonNull MarkwonHtmlParser parser
    );

    @Nullable
    public abstract TagHandler tagHandler(@NonNull String tagName);
}
