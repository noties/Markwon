package io.noties.markwon.test;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

class TestSpanSpan extends TestSpan.Span {

    private final String name;
    private final List<TestSpan> children;
    private final Map<String, Object> arguments;

    public TestSpanSpan(
            @NonNull String name,
            @NonNull List<TestSpan> children,
            @NonNull Map<String, Object> arguments) {
        this.name = name;
        this.children = children;
        this.arguments = arguments;
    }

    @NonNull
    @Override
    public String name() {
        return name;
    }

    @NonNull
    @Override
    public Map<String, Object> arguments() {
        return arguments;
    }

    @NonNull
    @Override
    public List<TestSpan> children() {
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestSpanSpan that = (TestSpanSpan) o;

        if (!name.equals(that.name)) return false;
        return arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + arguments.hashCode();
        return result;
    }
}
