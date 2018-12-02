package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.ThematicBreak;

import ru.noties.markwon.MarkwonVisitor;

public class ThematicBreakNodeVisitor implements MarkwonVisitor.NodeVisitor<ThematicBreak> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull ThematicBreak thematicBreak) {

        visitor.ensureNewLine();

        final int length = visitor.length();

        // without space it won't render
        visitor.builder().append('\u00a0');

        visitor.setSpans(length, visitor.factory().thematicBreak(visitor.theme()));

        if (visitor.hasNext(thematicBreak)) {
            visitor.ensureNewLine();
            visitor.forceNewLine();
        }
    }
}
