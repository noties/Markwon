package ru.noties.markwon;

import android.support.annotation.NonNull;

import org.commonmark.node.Node;

import java.util.Map;

public class AbstractMarkwonVisitorImpl extends MarkwonVisitorImpl {

    public AbstractMarkwonVisitorImpl(
            @NonNull MarkwonConfiguration configuration,
            @NonNull RenderProps renderProps,
            @NonNull SpannableBuilder spannableBuilder,
            @NonNull Map<Class<? extends Node>, NodeVisitor<? extends Node>> nodes) {
        super(configuration, renderProps, spannableBuilder, nodes);
    }
}
