package io.noties.markwon.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @see Inline
 * @see Block
 * @since 2.0.0
 */
public interface HtmlTag {

    int NO_END = -1;

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
     * @return flag indicating if this tag has no content (when start == end)
     */
    boolean isEmpty();

    /**
     * @return flag indicating if this tag is closed (has valid start and end)
     * @see #NO_END
     */
    boolean isClosed();

    @NonNull
    Map<String, String> attributes();

    /**
     * @see Inline
     */
    boolean isInline();

    /**
     * @see Block
     */
    boolean isBlock();

    @NonNull
    Inline getAsInline();

    @NonNull
    Block getAsBlock();

    /**
     * Represents <em>really</em> inline HTML tags (unlile commonmark definitions)
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

        /**
         * @return a flag indicating if this {@link Block} is at the root level (shortcut to calling:
         * {@code parent() == null}
         */
        boolean isRoot();
    }
}
