package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.Heading;

import ru.noties.markwon.MarkwonVisitor;

public class HeadingNodeVisitor implements MarkwonVisitor.NodeVisitor<Heading> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull Heading heading) {

        visitor.ensureNewLine();

        final int length = visitor.length();
        visitor.visitChildren(heading);
        visitor.setSpans(length, visitor.factory().heading(visitor.theme(), heading.getLevel()));

        if (visitor.hasNext(heading)) {
            visitor.ensureNewLine();
            visitor.forceNewLine();
        }
    }
}
