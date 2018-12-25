package ru.noties.markwon.recycler;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.commonmark.node.Node;

import java.util.List;

import ru.noties.markwon.Markwon;

/**
 * Class to display markdown in a RecyclerView. It is done by extracting root blocks from a
 * parsed markdown document and rendering each block in a standalone RecyclerView entry. Provides
 * ability to customize rendering of blocks. For example display certain blocks in a horizontal
 * scrolling container or display tables in a specific widget designed for it ({@link Builder#include(Class, Entry)}).
 * <p>
 * By default each node will be rendered in a TextView provided by this artifact. It has no styling
 * information and thus must be replaced with your own layout ({@link Builder#defaultEntry(int)} or
 * {@link Builder#defaultEntry(Entry)}).
 *
 * @see #builder()
 * @see #create()
 * @see #create(int)
 * @see #create(Entry)
 * @see #setMarkdown(Markwon, String)
 * @see #setParsedMarkdown(Markwon, Node)
 * @see #setParsedMarkdown(Markwon, List)
 * @since 3.0.0
 */
public abstract class MarkwonAdapter extends RecyclerView.Adapter<MarkwonAdapter.Holder> {

    @NonNull
    public static Builder builder() {
        return new MarkwonAdapterImpl.BuilderImpl();
    }

    @NonNull
    public static MarkwonAdapter create() {
        return new MarkwonAdapterImpl.BuilderImpl().build();
    }

    // for an adapter with only one entry (all blocks are rendered the same with this entry)
    @NonNull
    public static MarkwonAdapter create(@NonNull Entry<? extends Holder, ? extends Node> defaultEntry) {
        return new MarkwonAdapterImpl.BuilderImpl().defaultEntry(defaultEntry).build();
    }

    @NonNull
    public static MarkwonAdapter create(@LayoutRes int layoutResId) {
        return new MarkwonAdapterImpl.BuilderImpl().defaultEntry(layoutResId).build();
    }

    public interface Builder {

        @NonNull
        <N extends Node> Builder include(
                @NonNull Class<N> node,
                @NonNull Entry<? extends Holder, ? super N> entry);

        @NonNull
        Builder defaultEntry(@NonNull Entry<? extends Holder, ? extends Node> defaultEntry);

        @NonNull
        Builder defaultEntry(@LayoutRes int layoutResId);

        @NonNull
        Builder reducer(@NonNull Reducer reducer);

        @NonNull
        MarkwonAdapter build();
    }

    /**
     * @see SimpleEntry
     */
    public interface Entry<H extends Holder, N extends Node> {

        @NonNull
        H createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

        void bindHolder(@NonNull Markwon markwon, @NonNull H holder, @NonNull N node);

        long id(@NonNull N node);

        // will be called when new content is available (clear internal cache if any)
        void clear();
    }

    public interface Reducer {

        @NonNull
        List<Node> reduce(@NonNull Node root);
    }

    public abstract void setMarkdown(@NonNull Markwon markwon, @NonNull String markdown);

    public abstract void setParsedMarkdown(@NonNull Markwon markwon, @NonNull Node document);

    public abstract void setParsedMarkdown(@NonNull Markwon markwon, @NonNull List<Node> nodes);

    @SuppressWarnings("WeakerAccess")
    public static class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView) {
            super(itemView);
        }

        // please note that this method should be called after constructor
        @Nullable
        protected <V extends View> V findView(@IdRes int id) {
            return itemView.findViewById(id);
        }

        // please note that this method should be called after constructor
        @NonNull
        protected <V extends View> V requireView(@IdRes int id) {
            final V v = itemView.findViewById(id);
            if (v == null) {
                throw new NullPointerException();
            }
            return v;
        }
    }
}
