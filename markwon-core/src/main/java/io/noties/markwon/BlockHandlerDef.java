package io.noties.markwon;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;

/**
 * @since 4.3.0
 */
public class BlockHandlerDef implements MarkwonVisitor.BlockHandler {
    @Override
    public void blockStart(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
        visitor.ensureNewLine();
    }

    @Override
    public void blockEnd(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
        if (visitor.hasNext(node)) {
            visitor.ensureNewLine();
            visitor.forceNewLine();
        }
    }
}
