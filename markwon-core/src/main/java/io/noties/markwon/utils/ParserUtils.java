package io.noties.markwon.utils;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;

/**
 * @since 4.6.0
 */
public abstract class ParserUtils {

    public static void moveChildren(@NonNull Node to, @NonNull Node from) {
        Node next = from.getNext();
        Node temp;
        while (next != null) {
            // appendChild would unlink passed node (thus making next info un-available)
            temp = next.getNext();
            to.appendChild(next);
            next = temp;
        }
    }

    private ParserUtils() {
    }
}
