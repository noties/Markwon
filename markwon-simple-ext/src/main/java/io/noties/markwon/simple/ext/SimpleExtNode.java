package io.noties.markwon.simple.ext;

import androidx.annotation.NonNull;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Visitor;

import io.noties.markwon.SpanFactory;

// @since 4.0.0
class SimpleExtNode extends CustomNode {

    private final SpanFactory spanFactory;

    SimpleExtNode(@NonNull SpanFactory spanFactory) {
        this.spanFactory = spanFactory;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @NonNull
    SpanFactory spanFactory() {
        return spanFactory;
    }
}
