package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.SoftLineBreak;

import ru.noties.markwon.MarkwonVisitor;

public class SoftLineBreakNodeVisitor implements MarkwonVisitor.NodeVisitor<SoftLineBreak> {

    private final boolean softBreakAddsNewLine;

    public SoftLineBreakNodeVisitor(boolean softBreakAddsNewLine) {
        this.softBreakAddsNewLine = softBreakAddsNewLine;
    }

    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull SoftLineBreak softLineBreak) {
        if (softBreakAddsNewLine) {
            visitor.ensureNewLine();
        } else {
            visitor.builder().append(' ');
        }
    }
}
