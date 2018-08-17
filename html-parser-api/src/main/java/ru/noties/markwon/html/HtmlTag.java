package ru.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * @see Inline
 * @see Block
 */
public interface HtmlTag {

    /**
     * @return normalized tag name (lower-case)
     */
    @NonNull
    String name();

    /**
     * @return index at which this tag starts
     */
    int start();

    /**
     * @return index at which this tag ends
     */
    int end();

    /**
     * Represents <em>really</em> inline HTML tags (unline commonmark definitions)
     */
    interface Inline extends HtmlTag {
    }

    /**
     * Represents HTML block tags. Please note that all tags that are not inline should be
     * considered as block tags
     */
    interface Block extends HtmlTag {

        /**
         * @return parent {@link Block} or null if there is no parent (this block is at root level)
         */
        @Nullable
        Block parent();

        /**
         * @return list of children
         */
        @NonNull
        List<Block> children();
    }
}
