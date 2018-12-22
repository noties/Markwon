package ru.noties.markwon.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Node;

import ru.noties.markwon.MarkwonVisitor;

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
