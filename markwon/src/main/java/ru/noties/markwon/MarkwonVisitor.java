package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Node;
import org.commonmark.node.Visitor;

/**
 * @since 3.0.0
 */
public interface MarkwonVisitor extends Visitor {

    interface NodeVisitor<N extends Node> {
        void visit(@NonNull MarkwonVisitor visitor, @NonNull N n);
    }

    interface Builder {

        @NonNull
        <N extends Node> Builder on(@NonNull Class<N> node, @Nullable NodeVisitor<? super N> nodeVisitor);

        @NonNull
        MarkwonVisitor build(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps renderProps);
    }

    @NonNull
    MarkwonConfiguration configuration();

    @NonNull
    RenderProps renderProps();

    @NonNull
    SpannableBuilder builder();

    void visitChildren(@NonNull Node node);

    boolean hasNext(@NonNull Node node);

    void ensureNewLine();

    void forceNewLine();

    int length();

    void setSpans(int start, @Nullable Object spans);

    // will automatically obtain SpanFactory instance and use it, it no SpanFactory is registered,
    // will throw, if not desired use setSpansForNodeOptional
    <N extends Node> void setSpansForNode(@NonNull N node, int start);

    // does not throw if there is no SpanFactory registered for this node
    <N extends Node> void setSpansForNodeOptional(@NonNull N node, int start);
}
