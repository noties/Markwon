package ru.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.MarkwonVisitor;

/**
 * @since 2.0.0
 */
public abstract class MarkwonHtmlRenderer {

    @NonNull
    public static Builder builder() {
        return new MarkwonHtmlRendererImpl.BuilderImpl();
    }

    public abstract void render(
            @NonNull MarkwonVisitor visitor,
            @NonNull MarkwonHtmlParser parser
    );

    @Nullable
    public abstract TagHandler tagHandler(@NonNull String tagName);


    /**
     * @since 3.0.0
     */
    public interface Builder {

        /**
         * @param allowNonClosedTags parameter to indicate that all non-closed HTML tags should be
         *                           closed at the end of a document. if {@code true} all non-closed
         *                           tags will be force-closed at the end. Otherwise these tags will be
         *                           ignored and thus not rendered.
         * @return self
         */
        @NonNull
        Builder allowNonClosedTags(boolean allowNonClosedTags);

        /**
         * Please note that if there is already a {@link TagHandler} registered with specified
         * {@code tagName} it will be replaced with newly supplied one.
         *
         * @param tagHandler {@link TagHandler}
         * @param tagName    name of a tag
         * @return self
         */
        @NonNull
        Builder addHandler(@NonNull TagHandler tagHandler, @NonNull String tagName);

        @NonNull
        Builder addHandler(@NonNull TagHandler tagHandler, String... tagNames);

        @NonNull
        Builder removeHandler(@NonNull String tagName);

        @NonNull
        Builder removeHandlers(@NonNull String... tagNames);

        @NonNull
        MarkwonHtmlRenderer build();
    }
}
