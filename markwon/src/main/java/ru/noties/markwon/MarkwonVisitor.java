package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    interface Builder {

        /**
         * @param node        to register
         * @param nodeVisitor {@link NodeVisitor} to be used or null to ignore previously registered
         *                    visitor for this node
         */
        @NonNull
        <N extends Node> Builder on(@NonNull Class<N> node, @Nullable NodeVisitor<? super N> nodeVisitor);

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
     * Sets <code>spans</code> to underlying {@link SpannableBuilder} from <em>start</em>
     * to <em>{@link SpannableBuilder#length()}</em>.
     *
     * @param start start position of spans
     * @param spans to apply
     */
    void setSpans(int start, @Nullable Object spans);

    /**
     * Helper method to obtain and apply spans for supplied Node. Internally queries {@link SpanFactory}
     * for the node (via {@link MarkwonSpansFactory#require(Node)} thus throwing an exception
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
     * uses {@link MarkwonSpansFactory#require(Node)}. This method uses {@link MarkwonSpansFactory#get(Node)}.
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
}
