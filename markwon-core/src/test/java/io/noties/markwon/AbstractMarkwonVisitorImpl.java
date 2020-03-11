package io.noties.markwon;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;

import java.util.Map;

public class AbstractMarkwonVisitorImpl extends MarkwonVisitorImpl {

    public AbstractMarkwonVisitorImpl(
            @NonNull MarkwonConfiguration configuration,
            @NonNull RenderProps renderProps,
            @NonNull SpannableBuilder spannableBuilder,
            @NonNull Map<Class<? extends Node>, NodeVisitor<? extends Node>> nodes,
            @NonNull BlockHandler blockHandler) {
        super(configuration, renderProps, spannableBuilder, nodes, blockHandler);
    }
}
