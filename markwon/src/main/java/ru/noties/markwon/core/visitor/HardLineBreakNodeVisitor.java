package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.HardLineBreak;

import ru.noties.markwon.MarkwonVisitor;

public class HardLineBreakNodeVisitor implements MarkwonVisitor.NodeVisitor<HardLineBreak> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull HardLineBreak hardLineBreak) {
        visitor.ensureNewLine();
    }
}
