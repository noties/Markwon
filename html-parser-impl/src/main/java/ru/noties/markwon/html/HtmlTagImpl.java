package ru.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;

abstract class HtmlTagImpl implements HtmlTag {

    private static final int NO_VALUE = -1;

    final String name;
    final int start;
    int end = NO_VALUE;

    protected HtmlTagImpl(@NonNull String name, int start) {
        this.name = name;
        this.start = start;
    }

    @NonNull
    @Override
    public String name() {
        return name;
    }

    @Override
    public int start() {
        return start;
    }

    @Override
    public int end() {
        return end;
    }

    @Override
    public boolean isEmpty() {
        return start == end;
    }

    boolean isClosed() {
        return end > NO_VALUE;
    }

    abstract void closeAt(int end);

    static class InlineImpl extends HtmlTagImpl implements Inline {

        InlineImpl(@NonNull String name, int start) {
            super(name, start);
        }

        @Override
        void closeAt(int end) {
            if (!isClosed()) {
                super.end = end;
            }
        }

        @Override
        public String toString() {
            return "InlineImpl{" +
                    "name='" + name + '\'' +
                    ", start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    static class BlockImpl extends HtmlTagImpl implements Block {

        @NonNull
        static BlockImpl root() {
            //noinspection ConstantConditions
            return new BlockImpl("", 0, null);
        }

        @NonNull
        static BlockImpl create(@NonNull String name, int start, @NonNull BlockImpl parent) {
            return new BlockImpl(name, start, parent);
        }

        final BlockImpl parent;
        List<BlockImpl> children;

        @SuppressWarnings("NullableProblems")
        BlockImpl(@NonNull String name, int start, @NonNull BlockImpl parent) {
            super(name, start);
            this.parent = parent;
        }

        @Override
        void closeAt(int end) {
            if (!isClosed()) {
                super.end = end;
                if (children != null) {
                    for (BlockImpl child : children) {
                        child.closeAt(end);
                    }
                    children = Collections.unmodifiableList(children);
                } else {
                    children = Collections.emptyList();
                }
            }
        }

        boolean isRoot() {
            return parent == null;
        }

        @Nullable
        @Override
        public Block parent() {
            if (parent == null) {
                throw new IllegalStateException("#parent() getter was called on the root node " +
                        "which should not be exposed outside internal usage");
            }
            return parent;
        }

        @NonNull
        @Override
        public List<Block> children() {
            //noinspection unchecked
            return (List<Block>) (List<? extends Block>) children;
        }

        @Override
        public String toString() {
            return "BlockImpl{" +
                    "name='" + name + '\'' +
                    ", start=" + start +
                    ", end=" + end +
                    ", parent=" + (parent != null ? parent.name : null) +
                    ", children=" + children +
                    '}';
        }
    }
}
