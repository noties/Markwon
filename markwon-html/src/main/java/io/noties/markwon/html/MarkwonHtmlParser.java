package io.noties.markwon.html;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * @since 2.0.0
 */
public abstract class MarkwonHtmlParser {

    public interface FlushAction<T> {
        void apply(@NonNull List<T> tags);
    }

    public abstract <T extends Appendable & CharSequence> void processFragment(
            @NonNull T output,
            @NonNull String htmlFragment);

    /**
     * After this method exists a {@link MarkwonHtmlParser} will clear internal state for stored tags.
     * If you wish to process them further after this method exists create own copy of supplied
     * collection.
     *
     * @param documentLength known document length. This value is used to close all non-closed tags.
     *                       If you wish to keep them open (do not force close at the end of a
     *                       document pass here {@link HtmlTag#NO_END}. Later non-closed tags
     *                       can be detected by calling {@link HtmlTag#isClosed()}
     * @param action         {@link FlushAction} to be called with resulting tags ({@link HtmlTag.Inline})
     */
    public abstract void flushInlineTags(
            int documentLength,
            @NonNull FlushAction<HtmlTag.Inline> action);

    /**
     * After this method exists a {@link MarkwonHtmlParser} will clear internal state for stored tags.
     * If you wish to process them further after this method exists create own copy of supplied
     * collection.
     *
     * @param documentLength known document length. This value is used to close all non-closed tags.
     *                       If you wish to keep them open (do not force close at the end of a
     *                       document pass here {@link HtmlTag#NO_END}. Later non-closed tags
     *                       can be detected by calling {@link HtmlTag#isClosed()}
     * @param action         {@link FlushAction} to be called with resulting tags ({@link HtmlTag.Block})
     */
    public abstract void flushBlockTags(
            int documentLength,
            @NonNull FlushAction<HtmlTag.Block> action);

    public abstract void reset();

}
