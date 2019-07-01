package io.noties.markwon.test;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

class TestSpanText extends TestSpan.Text {

    private final String literal;

    TestSpanText(@NonNull String literal) {
        this.literal = literal;
    }

    @NonNull
    @Override
    public String literal() {
        return literal;
    }

    @Override
    public int length() {
        return literal.length();
    }

    @NonNull
    @Override
    public List<TestSpan> children() {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestSpanText that = (TestSpanText) o;

        return literal.equals(that.literal);
    }

    @Override
    public int hashCode() {
        return literal.hashCode();
    }
}
