package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.StrongEmphasis;

import ru.noties.markwon.MarkwonVisitor;

public class StrongEmphasisNodeVisitor implements MarkwonVisitor.NodeVisitor<StrongEmphasis> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull StrongEmphasis strongEmphasis) {
        final int length = visitor.length();
        visitor.visitChildren(strongEmphasis);
        visitor.setSpans(length, visitor.factory().strongEmphasis());
    }
}
