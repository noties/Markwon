package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Node;

import ru.noties.markwon.MarkwonVisitor;

public abstract class CodeBlockNodeVisitor {

    public static class Fenced implements MarkwonVisitor.NodeVisitor<FencedCodeBlock> {

        @Override
        public void visit(@NonNull MarkwonVisitor visitor, @NonNull FencedCodeBlock fencedCodeBlock) {
            visitCodeBlock(visitor, fencedCodeBlock.getInfo(), fencedCodeBlock.getLiteral(), fencedCodeBlock);
        }
    }

    public static class Indented implements MarkwonVisitor.NodeVisitor<IndentedCodeBlock> {

        @Override
        public void visit(@NonNull MarkwonVisitor visitor, @NonNull IndentedCodeBlock indentedCodeBlock) {
            visitCodeBlock(visitor, null, indentedCodeBlock.getLiteral(), indentedCodeBlock);
        }
    }


    public static void visitCodeBlock(
            @NonNull MarkwonVisitor visitor,
            @Nullable String info,
            @NonNull String code,
            @NonNull Node node) {

        visitor.ensureNewLine();

        final int length = visitor.length();

        visitor.builder()
                .append('\u00a0').append('\n')
                .append(visitor.configuration().syntaxHighlight().highlight(info, code));

        visitor.ensureNewLine();

        visitor.builder().append('\u00a0');

        visitor.setSpans(length, visitor.factory().code(visitor.theme(), true));

        if (visitor.hasNext(node)) {
            visitor.ensureNewLine();
            visitor.forceNewLine();
        }
    }
}
