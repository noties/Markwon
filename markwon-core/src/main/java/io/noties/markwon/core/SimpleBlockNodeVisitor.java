package io.noties.markwon.core;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;

import io.noties.markwon.MarkwonVisitor;

/**
 * A {@link MarkwonVisitor.NodeVisitor} that ensures that a markdown
 * block starts with a new line, all children are visited and if further content available
 * ensures a new line after self. Does not render any spans
 *
 * @since 3.0.0
 */
public class SimpleBlockNodeVisitor implements MarkwonVisitor.NodeVisitor<Node> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull Node node) {

        visitor.blockStart(node);

        // @since 3.0.1 we keep track of start in order to apply spans (optionally)
        final int length = visitor.length();

        visitor.visitChildren(node);

        // @since 3.0.1 we apply optional spans
        visitor.setSpansForNodeOptional(node, length);

        visitor.blockEnd(node);
    }
}
