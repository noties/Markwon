package io.noties.markwon.recycler;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;

import java.util.Collections;
import java.util.List;

import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonReducer;
import io.noties.markwon.iframe.ext.IFrameNode;

class MarkwonAdapterImpl extends MarkwonAdapter {

    private final SparseArray<Entry<Node, Holder>> entries;
    private final Entry<Node, Holder> defaultEntry;
    private final MarkwonReducer reducer;

    private LayoutInflater layoutInflater;

    private Markwon markwon;
    private List<Node> nodes;
    private int depth = 0;

    @SuppressWarnings("WeakerAccess")
    MarkwonAdapterImpl(
            @NonNull SparseArray<Entry<Node, Holder>> entries,
            @NonNull Entry<Node, Holder> defaultEntry,
            @NonNull MarkwonReducer reducer) {
        this.entries = entries;
        this.defaultEntry = defaultEntry;
        this.reducer = reducer;

        setHasStableIds(true);
    }

    @Override
    public void setMarkdown(@NonNull Markwon markwon, @NonNull String markdown) {
        setParsedMarkdown(markwon, markwon.parse(markdown));
    }

    @Override
    public void setMarkdown(@NonNull Markwon markwon, @NonNull String markdown, int depth) {
        this.depth = depth;
        setParsedMarkdown(markwon, markwon.parse(markdown));
    }

    @Override
    public void setParsedMarkdown(@NonNull Markwon markwon, @NonNull Node document) {
        setParsedMarkdown(markwon, reducer.reduce(document));
    }

    @Override
    public void setParsedMarkdown(@NonNull Markwon markwon, @NonNull List<Node> nodes) {
        // clear all entries before applying
        try {
            defaultEntry.clear();
            for (int i = 0, size = entries.size(); i < size; i++) {
                entries.valueAt(i).clear();
            }
            this.markwon = markwon;
            this.nodes = nodes;
        } catch (Exception e) {
            Log.e("Markdown issue", nodes.toString());
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        final Entry<Node, Holder> entry = getEntry(viewType);
        return entry.createHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final Node node = nodes.get(position);
        int viewType = getNodeViewType(node);
        final Entry<Node, Holder> entry = getEntry(viewType);
        if (isIFrameTypeType(node)) {
            if(node.getFirstChild() != null) {
                entry.bindHolder(markwon, holder, node.getFirstChild().getFirstChild(), this.depth);
            }
        } else {
            entry.bindHolder(markwon, holder, node, this.depth);
        }
    }

    @Override
    public int getItemCount() {
        return nodes != null
                ? nodes.size()
                : 0;
    }

    @Override
    public void onViewRecycled(@NonNull Holder holder) {
        super.onViewRecycled(holder);

        final Entry<Node, Holder> entry = getEntry(holder.getItemViewType());
        entry.onViewRecycled(holder);
    }

    @SuppressWarnings("unused")
    @NonNull
    public List<Node> getItems() {
        return nodes != null
                ? Collections.unmodifiableList(nodes)
                : Collections.<Node>emptyList();
    }

    @Override
    public int getItemViewType(int position) {
        final Node node = nodes.get(position);
        return getNodeViewType(node);
    }

    @Override
    public long getItemId(int position) {
        final Node node = nodes.get(position);
        int type = getNodeViewType(node);
        final Entry<Node, Holder> entry = getEntry(type);
        return entry.id(node);
    }

    private boolean isIFrameTypeType(Node node) {
        if (node.getFirstChild() != null) {
            if (node.getFirstChild().toString().contains("IFrameGroupNode") &&
                    node.getFirstChild().toString().contains("IFrameGroupNode")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getNodeViewType(@NonNull Node node) {
        // if has registered -> then return it, else 0
        if (isIFrameTypeType(node)) {
            return IFrameNode.class.hashCode();
        }
        final int hash = node.getClass().hashCode();
        if (entries.indexOfKey(hash) > -1) {
            Log.d("NodeType1", String.valueOf(hash));
            return hash;
        }
        return 0;
    }

    @NonNull
    private Entry<Node, Holder> getEntry(int viewType) {
        return viewType == 0
                ? defaultEntry
                : entries.get(viewType);
    }

    static class BuilderImpl implements Builder {

        private final SparseArray<Entry<Node, Holder>> entries = new SparseArray<>(3);

        private final Entry<Node, Holder> defaultEntry;

        private MarkwonReducer reducer;

        BuilderImpl(@NonNull Entry<Node, Holder> defaultEntry) {
            this.defaultEntry = defaultEntry;
        }

        @NonNull
        @Override
        public <N extends Node> Builder include(
                @NonNull Class<N> node,
                @NonNull Entry<? super N, ? extends Holder> entry) {
            //noinspection unchecked
            entries.append(node.hashCode(), (Entry<Node, Holder>) entry);
            return this;
        }

        @NonNull
        @Override
        public Builder reducer(@NonNull MarkwonReducer reducer) {
            this.reducer = reducer;
            return this;
        }

        @NonNull
        @Override
        public MarkwonAdapter build() {

            if (reducer == null) {
                reducer = MarkwonReducer.directChildren();
            }

            return new MarkwonAdapterImpl(entries, defaultEntry, reducer);
        }
    }
}