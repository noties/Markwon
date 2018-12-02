package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.Node;

import ru.noties.markwon.MarkwonVisitor;

public class ListBlockNodeVisitor implements MarkwonVisitor.NodeVisitor<Node> {
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
