package ru.noties.markwon.core;

import android.support.annotation.NonNull;

import org.commonmark.node.Node;

import ru.noties.markwon.MarkwonVisitor;

/**
 * A {@link ru.noties.markwon.MarkwonVisitor.NodeVisitor} that ensures that a markdown
 * block starts with a new line, all children are visited and if further content available
 * ensures a new line after self. Does not render any spans
 *
 * @since 3.0.0
 */
public class SimpleBlockNodeVisitor implements MarkwonVisitor.NodeVisitor<Node> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull Node node) {

        visitor.ensureNewLine();

        visitor.visitChildren(node);

        if (visitor.hasNext(node)) {
            visitor.ensureNewLine();
            visitor.forceNewLine();
        }
    }
}
