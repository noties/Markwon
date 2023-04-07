package io.noties.markwon.simple.ext;

import androidx.annotation.NonNull;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import org.jetbrains.annotations.NotNull;

import io.noties.markwon.SpanFactory;

// @since 4.0.0
class SimpleExtNode extends Node {

    private final SpanFactory spanFactory;

    SimpleExtNode(@NonNull SpanFactory spanFactory) {
        this.spanFactory = spanFactory;
    }

    @NonNull
    SpanFactory spanFactory() {
        return spanFactory;
    }

    @NonNull
    @Override
    public @NotNull BasedSequence[] getSegments() {
        return BasedSequence.EMPTY_SEGMENTS;
    }
}
