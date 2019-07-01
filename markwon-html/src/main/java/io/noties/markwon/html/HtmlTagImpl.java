package io.noties.markwon.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract class HtmlTagImpl implements HtmlTag {

    final String name;
    final int start;
    final Map<String, String> attributes;
    int end = NO_END;

    protected HtmlTagImpl(@NonNull String name, int start, @NonNull Map<String, String> attributes) {
        this.name = name;
        this.start = start;
        this.attributes = attributes;
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

    @NonNull
    @Override
    public Map<String, String> attributes() {
        return attributes;
    }

    @Override
    public boolean isClosed() {
        return end > NO_END;
    }

    abstract void closeAt(int end);


    static class InlineImpl extends HtmlTagImpl implements Inline {

        InlineImpl(@NonNull String name, int start, @NonNull Map<String, String> attributes) {
            super(name, start, attributes);
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
                    ", attributes=" + attributes +
                    '}';
        }

        @Override
        public boolean isInline() {
            return true;
        }

        @Override
        public boolean isBlock() {
            return false;
        }

        @NonNull
        @Override
        public Inline getAsInline() {
            return this;
        }

        @NonNull
        @Override
        public Block getAsBlock() {
            throw new ClassCastException("Cannot cast Inline instance to Block");
        }
    }

    static class BlockImpl extends HtmlTagImpl implements Block {

        @NonNull
        static BlockImpl root() {
            return new BlockImpl("", 0, Collections.<String, String>emptyMap(), null);
        }

        @NonNull
        static BlockImpl create(
                @NonNull String name,
                int start,
                @NonNull Map<String, String> attributes,
                @Nullable BlockImpl parent) {
            return new BlockImpl(name, start, attributes, parent);
        }

        final BlockImpl parent;
        List<BlockImpl> children;

        @SuppressWarnings("NullableProblems")
        BlockImpl(
                @NonNull String name,
                int start,
                @NonNull Map<String, String> attributes,
                @Nullable BlockImpl parent) {
            super(name, start, attributes);
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
                }
            }
        }

        @Override
        public boolean isRoot() {
            return parent == null;
        }

        @Nullable
        @Override
        public Block parent() {
            return parent;
        }

        @NonNull
        @Override
        public List<Block> children() {
            final List<Block> list;
            if (children == null) {
                list = Collections.emptyList();
            } else {
                list = Collections.unmodifiableList((List<? extends Block>) children);
            }
            return list;
        }

        @NonNull
        @Override
        public Map<String, String> attributes() {
            return attributes;
        }

        @Override
        public boolean isInline() {
            return false;
        }

        @Override
        public boolean isBlock() {
            return true;
        }

        @NonNull
        @Override
        public Inline getAsInline() {
            throw new ClassCastException("Cannot cast Block instance to Inline");
        }

        @NonNull
        @Override
        public Block getAsBlock() {
            return this;
        }

        @Override
        public String toString() {
            return "BlockImpl{" +
                    "name='" + name + '\'' +
                    ", start=" + start +
                    ", end=" + end +
                    ", attributes=" + attributes +
                    ", parent=" + (parent != null ? parent.name : null) +
                    ", children=" + children +
                    '}';
        }
    }
}
