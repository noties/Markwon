package io.noties.markwon;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.commonmark.node.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 3.0.0
 */
public abstract class MarkwonNodeRenderer {

    public interface ViewProvider<N extends Node> {

        /**
         * Please note that you should not attach created View to specified group. It will be done
         * automatically.
         */
        @NonNull
        View provide(
                @NonNull LayoutInflater inflater,
                @NonNull ViewGroup group,
                @NonNull Markwon markwon,
                @NonNull N n);
    }

    @NonNull
    public static Builder builder(@NonNull ViewProvider<Node> defaultViewProvider) {
        return new Builder(defaultViewProvider);
    }

    /**
     * @param defaultViewProviderLayoutResId layout resource id to be used in default view provider
     * @param defaultViewProviderTextViewId  id of a TextView in specified layout
     * @return Builder
     * @see SimpleTextViewProvider
     */
    @NonNull
    public static Builder builder(
            @LayoutRes int defaultViewProviderLayoutResId,
            @IdRes int defaultViewProviderTextViewId) {
        return new Builder(new SimpleTextViewProvider(
                defaultViewProviderLayoutResId,
                defaultViewProviderTextViewId));
    }

    public abstract void render(@NonNull ViewGroup group, @NonNull Markwon markwon, @NonNull String markdown);

    public abstract void render(@NonNull ViewGroup group, @NonNull Markwon markwon, @NonNull Node root);


    public static class Builder {

        private final ViewProvider<Node> defaultViewProvider;

        private MarkwonReducer reducer;
        private Map<Class<? extends Node>, ViewProvider<Node>> viewProviders;
        private LayoutInflater inflater;

        public Builder(@NonNull ViewProvider<Node> defaultViewProvider) {
            this.defaultViewProvider = defaultViewProvider;
            this.viewProviders = new HashMap<>(3);
        }

        @NonNull
        public Builder reducer(@NonNull MarkwonReducer reducer) {
            this.reducer = reducer;
            return this;
        }

        @NonNull
        public <N extends Node> Builder viewProvider(
                @NonNull Class<N> type,
                @NonNull ViewProvider<? super N> viewProvider) {
            //noinspection unchecked
            viewProviders.put(type, (ViewProvider<Node>) viewProvider);
            return this;
        }

        @NonNull
        public Builder inflater(@NonNull LayoutInflater inflater) {
            this.inflater = inflater;
            return this;
        }

        @NonNull
        public MarkwonNodeRenderer build() {
            if (reducer == null) {
                reducer = MarkwonReducer.directChildren();
            }
            return new Impl(this);
        }
    }

    public static class SimpleTextViewProvider implements ViewProvider<Node> {

        private final int layoutResId;
        private final int textViewId;

        public SimpleTextViewProvider(@LayoutRes int layoutResId, @IdRes int textViewId) {
            this.layoutResId = layoutResId;
            this.textViewId = textViewId;
        }

        @NonNull
        @Override
        public View provide(
                @NonNull LayoutInflater inflater,
                @NonNull ViewGroup group,
                @NonNull Markwon markwon,
                @NonNull Node node) {
            final View view = inflater.inflate(layoutResId, group, false);
            final TextView textView = view.findViewById(textViewId);
            markwon.setParsedMarkdown(textView, markwon.render(node));
            return view;
        }
    }

    static class Impl extends MarkwonNodeRenderer {

        private final MarkwonReducer reducer;
        private final Map<Class<? extends Node>, ViewProvider<Node>> viewProviders;
        private final ViewProvider<Node> defaultViewProvider;

        private LayoutInflater inflater;

        Impl(@NonNull Builder builder) {
            this.reducer = builder.reducer;
            this.viewProviders = builder.viewProviders;
            this.defaultViewProvider = builder.defaultViewProvider;
            this.inflater = builder.inflater;
        }

        @Override
        public void render(@NonNull ViewGroup group, @NonNull Markwon markwon, @NonNull String markdown) {
            render(group, markwon, markwon.parse(markdown));
        }

        @Override
        public void render(@NonNull ViewGroup group, @NonNull Markwon markwon, @NonNull Node root) {

            final LayoutInflater inflater = ensureLayoutInflater(group.getContext());

            ViewProvider<Node> viewProvider;

            for (Node node : reducer.reduce(root)) {
                viewProvider = viewProvider(node);
                group.addView(viewProvider.provide(inflater, group, markwon, node));
            }
        }

        @NonNull
        private LayoutInflater ensureLayoutInflater(@NonNull Context context) {
            LayoutInflater inflater = this.inflater;
            if (inflater == null) {
                inflater = this.inflater = LayoutInflater.from(context);
            }
            return inflater;
        }

        @NonNull
        private ViewProvider<Node> viewProvider(@NonNull Node node) {

            // check for specific node view provider
            final ViewProvider<Node> provider = viewProviders.get(node.getClass());
            if (provider != null) {
                return provider;
            }

            // if it's not present, then we can return a default one
            return defaultViewProvider;
        }
    }

}
