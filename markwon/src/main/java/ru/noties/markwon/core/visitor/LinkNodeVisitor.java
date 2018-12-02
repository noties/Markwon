package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.Link;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonVisitor;

public class LinkNodeVisitor implements MarkwonVisitor.NodeVisitor<Link> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull Link link) {
        final int length = visitor.length();
        visitor.visitChildren(link);
        final MarkwonConfiguration configuration = visitor.configuration();
        final String destination = configuration.urlProcessor().process(link.getDestination());
        visitor.setSpans(length, visitor.factory().link(visitor.theme(), destination, configuration.linkResolver()));
    }
}
