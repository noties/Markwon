package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.Text;

import ru.noties.markwon.MarkwonVisitor;

public class TextNodeVisitor implements MarkwonVisitor.NodeVisitor<Text> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull Text text) {
        visitor.builder().append(text.getLiteral());
    }
}
