package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

abstract class TestNode {

    private final TestNode parent;

    TestNode(@Nullable TestNode parent) {
        this.parent = parent;
    }

    @Nullable
    public TestNode parent() {
        return parent;
    }

    abstract boolean isText();

    abstract boolean isSpan();

    @NonNull
    abstract Text getAsText();

    @NonNull
    abstract Span getAsSpan();


    static class Text extends TestNode {

        private final String text;

        Text(@Nullable TestNode parent, @NonNull String text) {
            super(parent);
            this.text = text;
        }

        @NonNull
        public String text() {
            return text;
        }

        @Override
        boolean isText() {
            return true;
        }

        @Override
        boolean isSpan() {
            return false;
        }

        @NonNull
        @Override
        Text getAsText() {
            return this;
        }

        @NonNull
        @Override
        Span getAsSpan() {
            throw new ClassCastException();
        }

        @Override
        public String toString() {
            return "Text{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }

    static class Span extends TestNode {

        private final String name;
        private final List<TestNode> children;
        private final Map<String, String> attributes;

        Span(
                @Nullable TestNode parent,
                @NonNull String name,
                @NonNull List<TestNode> children,
                @NonNull Map<String, String> attributes) {
            super(parent);
            this.name = name;
            this.children = children;
            this.attributes = attributes;
        }

        @NonNull
        public String name() {
            return name;
        }

        @NonNull
        public List<TestNode> children() {
            return children;
        }

        @NonNull
        public Map<String, String> attributes() {
            return attributes;
        }

        @Override
        boolean isText() {
            return false;
        }

        @Override
        boolean isSpan() {
            return true;
        }

        @NonNull
        @Override
        Text getAsText() {
            throw new ClassCastException();
        }

        @NonNull
        @Override
        Span getAsSpan() {
            return this;
        }

        @Override
        public String toString() {
            return "Span{" +
                    "name='" + name + '\'' +
                    ", children=" + children +
                    ", attributes=" + attributes +
                    '}';
        }
    }
}
