package ru.noties.markwon.core.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;

import ru.noties.markwon.MarkwonVisitor;

public class ListItemNodeVisitor implements MarkwonVisitor.NodeVisitor<ListItem> {

    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull ListItem listItem) {

        final int length = visitor.length();

        final Node parent = listItem.getParent();
        if (parent instanceof OrderedList) {

            final int start = ((OrderedList) parent).getStartNumber();

            visitor.visitChildren(listItem);
            visitor.setSpans(length, visitor.factory().orderedListItem(visitor.theme(), start));


            // after we have visited the children increment start number
            final OrderedList orderedList = (OrderedList) parent;
            orderedList.setStartNumber(orderedList.getStartNumber() + 1);

        } else {

            visitor.visitChildren(listItem);
            visitor.setSpans(length, visitor.factory().bulletListItem(visitor.theme(), listLevel(listItem)));

        }

        if (visitor.hasNext(listItem)) {
            visitor.ensureNewLine();
        }
    }

    private static int listLevel(@NonNull Node node) {
        int level = 0;
        Node parent = node.getParent();
        while (parent != null) {
            if (parent instanceof ListItem) {
                level += 1;
            }
            parent = parent.getParent();
        }
        return level;
    }
}
