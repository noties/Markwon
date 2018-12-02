package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.Emphasis;

import ru.noties.markwon.MarkwonVisitor;

public class EmphasisNodeVisitor implements MarkwonVisitor.NodeVisitor<Emphasis> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull Emphasis emphasis) {
        final int length = visitor.length();
        visitor.visitChildren(emphasis);
        visitor.setSpans(length, visitor.factory().emphasis());
    }
}
