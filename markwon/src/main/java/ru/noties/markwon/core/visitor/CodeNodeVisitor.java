package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.Code;

import ru.noties.markwon.MarkwonVisitor;

public class CodeNodeVisitor implements MarkwonVisitor.NodeVisitor<Code> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull Code code) {

        final int length = visitor.length();

        // NB, in order to provide a _padding_ feeling code is wrapped inside two unbreakable spaces
        // unfortunately we cannot use this for multiline code as we cannot control where a new line break will be inserted
        visitor.builder()
                .append('\u00a0')
                .append(code.getLiteral())
                .append('\u00a0');

        visitor.setSpans(length, visitor.factory().code(visitor.theme(), false));
    }
}
