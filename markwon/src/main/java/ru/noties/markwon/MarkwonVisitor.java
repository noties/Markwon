package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Node;
import org.commonmark.node.Visitor;

import ru.noties.markwon.spans.MarkwonTheme;

public interface MarkwonVisitor extends Visitor {

    interface NodeVisitor<N extends Node> {
        void visit(@NonNull MarkwonVisitor visitor, @NonNull N n);
    }

    interface Builder {

        @NonNull
        <N extends Node> Builder on(@NonNull Class<N> node, @Nullable NodeVisitor<? super N> nodeVisitor);

        // to obtain currently registered one
        @Nullable
        <N extends Node> NodeVisitor<N> registeredVisitor(@NonNull Class<N> node);

        @NonNull
        MarkwonVisitor build(@NonNull MarkwonConfiguration configuration);
    }

    @NonNull
    MarkwonConfiguration configuration();

    @NonNull
    MarkwonTheme theme();

    @NonNull
    SpannableFactory factory();

    @NonNull
    SpannableBuilder builder();

    void visitChildren(@NonNull Node node);

    boolean hasNext(@NonNull Node node);

    void incrementBlockIndent();

    void decrementBlockIndent();

    void blockIntent(int blockIndent);

    int blockIndent();

    void incrementListLevel();

    void decrementListLevel();

    int listLevel();

    void ensureNewLine();

    void forceNewLine();

    int length();

    void setSpans(int start, @Nullable Object spans);
}
