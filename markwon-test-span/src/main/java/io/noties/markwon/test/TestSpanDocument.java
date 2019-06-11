package io.noties.markwon.test;

import androidx.annotation.NonNull;

import java.util.List;

class TestSpanDocument extends TestSpan.Document {

    private static void fillWholeText(@NonNull StringBuilder builder, @NonNull TestSpan span) {
        if (span instanceof Text) {
            builder.append(((Text) span).literal());
        } else if (span instanceof Span) {
            for (TestSpan child : span.children()) {
                fillWholeText(builder, child);
            }
        } else {
            throw new IllegalStateException("Unexpected state. Found unexpected TestSpan " +
                    "object of type `" + span.getClass().getName() + "`");
        }
    }

    private final List<TestSpan> children;

    TestSpanDocument(@NonNull List<TestSpan> children) {
        this.children = children;
    }

    @NonNull
    @Override
    public List<TestSpan> children() {
        return children;
    }

    @NonNull
    @Override
    public String wholeText() {
        final StringBuilder builder = new StringBuilder();

        for (TestSpan child : children) {
            fillWholeText(builder, child);
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestSpanDocument that = (TestSpanDocument) o;

        return children.equals(that.children);
    }

    @Override
    public int hashCode() {
        return children.hashCode();
    }
}
