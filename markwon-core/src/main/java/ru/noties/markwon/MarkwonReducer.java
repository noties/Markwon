package ru.noties.markwon;

import android.support.annotation.NonNull;

import org.commonmark.node.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 3.0.0
 */
public abstract class MarkwonReducer {

    @NonNull
    public static MarkwonReducer directChildren() {
        return new DirectChildren();
    }

    @NonNull
    public abstract List<Node> reduce(@NonNull Node node);


    static class DirectChildren extends MarkwonReducer {

        @NonNull
        @Override
        public List<Node> reduce(@NonNull Node root) {

            final List<Node> list;

            // we will extract all blocks that are direct children of Document
            Node node = root.getFirstChild();

            // please note, that if there are no children -> we will return a list with
            // single element (which was supplied)
            if (node == null) {
                list = Collections.singletonList(root);
            } else {

                list = new ArrayList<>();

                Node temp;

                while (node != null) {
                    list.add(node);
                    temp = node.getNext();
                    node.unlink();
                    node = temp;
                }
            }

            return list;
        }
    }
}
