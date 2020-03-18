package io.noties.markwon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Node;
import org.commonmark.node.Visitor;

/**
 * Configurable visitor of parsed markdown. Allows visiting certain (registered) nodes without
 * need to create own instance of this class.
 *
 * @see Builder#on(Class, NodeVisitor)
 * @see MarkwonPlugin#configureVisitor(Builder)
 * @since 3.0.0
 */
public interface MarkwonVisitor extends Visitor {

    /**
     * @see Builder#on(Class, NodeVisitor)
     */
    interface NodeVisitor<N extends Node> {
        void visit(@NonNull MarkwonVisitor visitor, @NonNull N n);
    }

    /**
     * Primary purpose is to control the spacing applied before/after certain blocks, which
     * visitors are created elsewhere
     *
     * @since 4.3.0
     */
    interface BlockHandler {

        void blockStart(@NonNull MarkwonVisitor visitor, @NonNull Node node);

        void blockEnd(@NonNull MarkwonVisitor visitor, @NonNull Node node);
    }

    interface Builder {

        /**
         * @param node        to register
         * @param nodeVisitor {@link NodeVisitor} to be used or null to ignore previously registered
         *                    visitor for this node
         */
        @NonNull
        <N extends Node> Builder on(@NonNull Class<N> node, @Nullable NodeVisitor<? super N> nodeVisitor);

        /**
         * @param blockHandler to handle block start/end
         * @see BlockHandler
         * @see BlockHandlerDef
         * @since 4.3.0
         */
        @SuppressWarnings("UnusedReturnValue")
        @NonNull
        Builder blockHandler(@NonNull BlockHandler blockHandler);

        @NonNull
        MarkwonVisitor build(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps renderProps);
    }

    @NonNull
    MarkwonConfiguration configuration();

    @NonNull
    RenderProps renderProps();

    @NonNull
    SpannableBuilder builder();

    /**
     * Visits all children of supplied node.
     *
     * @param node to visit
     */
    void visitChildren(@NonNull Node node);

    /**
     * Executes a check if there is further content available.
     *
     * @param node to check
     * @return boolean indicating if there are more nodes after supplied one
     */
    boolean hasNext(@NonNull Node node);

    /**
     * This method <strong>ensures</strong> that further content will start at a new line. If current
     * last character is already a new line, then it won\'t do anything.
     */
    void ensureNewLine();

    /**
     * This method inserts a new line without any condition checking (unlike {@link #ensureNewLine()}).
     */
    void forceNewLine();

    /**
     * Helper method to call <code>builder().length()</code>
     *
     * @return current length of underlying {@link SpannableBuilder}
     */
    int length();

    /**
     * Clears state of visitor (both {@link RenderProps} and {@link SpannableBuilder} will be cleared
     */
    void clear();

    /**
     * Sets <code>spans</code> to underlying {@link SpannableBuilder} from <em>start</em>
     * to <em>{@link SpannableBuilder#length()}</em>.
     *
     * @param start start position of spans
     * @param spans to apply
     */
    void setSpans(int start, @Nullable Object spans);

    /**
     * Helper method to obtain and apply spans for supplied Node. Internally queries {@link SpanFactory}
     * for the node (via {@link MarkwonSpansFactory#require(Class)} thus throwing an exception
     * if there is no {@link SpanFactory} registered for the node).
     *
     * @param node  to retrieve {@link SpanFactory} for
     * @param start start position for further {@link #setSpans(int, Object)} call
     * @see #setSpansForNodeOptional(Node, int)
     */
    <N extends Node> void setSpansForNode(@NonNull N node, int start);

    /**
     * The same as {@link #setSpansForNode(Node, int)} but can be used in situations when there is
     * no access to a Node instance (for example in HTML rendering which doesn\'t have markdown Nodes).
     *
     * @see #setSpansForNode(Node, int)
     */
    <N extends Node> void setSpansForNode(@NonNull Class<N> node, int start);

    // does not throw if there is no SpanFactory registered for this node

    /**
     * Helper method to apply spans from a {@link SpanFactory} <b>if</b> it\'s registered in
     * {@link MarkwonSpansFactory} instance. Otherwise ignores this call (no spans will be applied).
     * If there is a need to ensure that specified <code>node</code> has a {@link SpanFactory} registered,
     * then {@link #setSpansForNode(Node, int)} can be used. {@link #setSpansForNode(Node, int)} internally
     * uses {@link MarkwonSpansFactory#require(Class)}. This method uses {@link MarkwonSpansFactory#get(Class)}.
     *
     * @see #setSpansForNode(Node, int)
     */
    <N extends Node> void setSpansForNodeOptional(@NonNull N node, int start);

    /**
     * The same as {@link #setSpansForNodeOptional(Node, int)} but can be used in situations when
     * there is no access to a Node instance (for example in HTML rendering).
     *
     * @see #setSpansForNodeOptional(Node, int)
     */
    @SuppressWarnings("unused")
    <N extends Node> void setSpansForNodeOptional(@NonNull Class<N> node, int start);

    /**
     * @since 4.3.0
     */
    void blockStart(@NonNull Node node);

    /**
     * @since 4.3.0
     */
    void blockEnd(@NonNull Node node);
}
