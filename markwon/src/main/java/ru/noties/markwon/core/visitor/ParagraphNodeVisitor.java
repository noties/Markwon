package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.ListBlock;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;

import ru.noties.markwon.MarkwonVisitor;

public class ParagraphNodeVisitor implements MarkwonVisitor.NodeVisitor<Paragraph> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull Paragraph paragraph) {

        final boolean inTightList = isInTightList(paragraph);

        if (!inTightList) {
            visitor.ensureNewLine();
        }

        final int length = visitor.length();
        visitor.visitChildren(paragraph);

        // @since 1.1.1 apply paragraph span
        visitor.setSpans(length, visitor.factory().paragraph(inTightList));

        if (!inTightList && visitor.hasNext(paragraph)) {
            visitor.ensureNewLine();
            visitor.forceNewLine();
        }
    }

    private static boolean isInTightList(@NonNull Paragraph paragraph) {
        final Node parent = paragraph.getParent();
        if (parent != null) {
            final Node gramps = parent.getParent();
            if (gramps instanceof ListBlock) {
                ListBlock list = (ListBlock) gramps;
                return list.isTight();
            }
        }
        return false;
    }
}
