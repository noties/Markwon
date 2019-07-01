package io.noties.markwon.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Node;

import io.noties.markwon.MarkwonVisitor;

public abstract class CorePluginBridge {

    public static void visitCodeBlock(
            @NonNull MarkwonVisitor visitor,
            @Nullable String info,
            @NonNull String code,
            @NonNull Node node) {
        CorePlugin.visitCodeBlock(visitor, info, code, node);
    }

    private CorePluginBridge() {
    }
}
