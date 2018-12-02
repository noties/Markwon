package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.BlockQuote;

import ru.noties.markwon.MarkwonVisitor;

public class BlockQuoteNodeVisitor implements MarkwonVisitor.NodeVisitor<BlockQuote> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull BlockQuote blockQuote) {

        visitor.ensureNewLine();

        final int length = visitor.length();

        visitor.visitChildren(blockQuote);
        visitor.setSpans(length, visitor.factory().blockQuote(visitor.theme()));

        if (visitor.hasNext(blockQuote)) {
            visitor.ensureNewLine();
            visitor.forceNewLine();
        }
    }
}
