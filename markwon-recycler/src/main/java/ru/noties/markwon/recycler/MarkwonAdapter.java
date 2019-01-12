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
 * Adapter to display markdown in a RecyclerView. It is done by extracting root blocks from a
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

    /**
     * Factory method to obtain {@link Builder} instance.
     *
     * @see Builder
     */
    @NonNull
    public static Builder builder() {
        return new MarkwonAdapterImpl.BuilderImpl();
    }

    /**
     * Factory method to create a {@link MarkwonAdapter} for evaluation purposes. Resulting
     * adapter will use default layout for all blocks. Default layout has no styling and should
     * be specified explicitly.
     *
     * @see #create(int)
     * @see #create(Entry)
     */
    @NonNull
    public static MarkwonAdapter create() {
        return new MarkwonAdapterImpl.BuilderImpl().build();
    }

    /**
     * Factory method to create a {@link MarkwonAdapter} that uses supplied entry to render all
     * nodes.
     *
     * @param defaultEntry {@link Entry} to be used for node rendering
     * @see SimpleEntry
     */
    @NonNull
    public static MarkwonAdapter create(@NonNull Entry<? extends Holder, ? extends Node> defaultEntry) {
        return new MarkwonAdapterImpl.BuilderImpl().defaultEntry(defaultEntry).build();
    }

    /**
     * Factory method to create a {@link MarkwonAdapter} that will use supplied layoutResId view
     * to display all nodes.
     *
     * <strong>Please note</strong> that supplied layout must have a TextView inside
     * with {@code android:id="@+id/text"}
     *
     * @param layoutResId layout to be used to display all nodes
     * @see SimpleEntry
     */
    @NonNull
    public static MarkwonAdapter create(@LayoutRes int layoutResId) {
        return new MarkwonAdapterImpl.BuilderImpl().defaultEntry(layoutResId).build();
    }

    /**
     * Builder to create an instance of {@link MarkwonAdapter}
     *
     * @see #include(Class, Entry)
     * @see #defaultEntry(int)
     * @see #defaultEntry(Entry)
     * @see #reducer(Reducer)
     */
    public interface Builder {

        /**
         * Include a custom {@link Entry} rendering for a Node. Please note that `node` argument
         * must be <em>exact</em> type, as internally there is no validation for inheritance. if multiple
         * nodes should be rendered with the same {@link Entry} they must specify so explicitly.
         * By calling this method for each.
         *
         * @param node  type of the node to register
         * @param entry {@link Entry} to be used for `node` rendering
         * @return self
         */
        @NonNull
        <N extends Node> Builder include(
                @NonNull Class<N> node,
                @NonNull Entry<? extends Holder, ? super N> entry);

        /**
         * Specify which {@link Entry} to use for all non-explicitly registered nodes
         *
         * @param defaultEntry {@link Entry}
         * @return self
         * @see SimpleEntry
         */
        @NonNull
        Builder defaultEntry(@NonNull Entry<? extends Holder, ? extends Node> defaultEntry);

        /**
         * Specify which layout {@link SimpleEntry} will use to render all non-explicitly
         * registered nodes.
         *
         * <strong>Please note</strong> that supplied layout must have a TextView inside
         * with {@code android:id="@+id/text"}
         *
         * @return self
         * @see SimpleEntry
         */
        @NonNull
        Builder defaultEntry(@LayoutRes int layoutResId);

        /**
         * Specify how root Node will be <em>reduced</em> to a list of nodes. There is a default
         * {@link Reducer} that will be used if not provided explicitly (there is no need to
         * register your own unless you require it).
         *
         * @param reducer {@link Reducer}
         * @return self
         * @see Reducer
         */
        @NonNull
        Builder reducer(@NonNull Reducer reducer);

        /**
         * @return {@link MarkwonAdapter}
         */
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

    public abstract int getNodeViewType(@NonNull Class<? extends Node> node);

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
