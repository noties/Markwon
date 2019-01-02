package ru.noties.markwon.recycler;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.commonmark.node.Node;

import java.util.HashMap;
import java.util.Map;

import ru.noties.markwon.Markwon;

/**
 * @since 3.0.0
 */
@SuppressWarnings("WeakerAccess")
public class SimpleEntry implements MarkwonAdapter.Entry<SimpleEntry.Holder, Node> {

    public static final Spannable.Factory NO_COPY_SPANNABLE_FACTORY = new NoCopySpannableFactory();

    // small cache for already rendered nodes
    private final Map<Node, Spanned> cache = new HashMap<>();

    private final int layoutResId;

    public SimpleEntry() {
        this(R.layout.markwon_adapter_simple_entry);
    }

    public SimpleEntry(@LayoutRes int layoutResId) {
        this.layoutResId = layoutResId;
    }

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(inflater.inflate(layoutResId, parent, false));
    }

    @Override
    public void bindHolder(@NonNull Markwon markwon, @NonNull Holder holder, @NonNull Node node) {
        Spanned spanned = cache.get(node);
        if (spanned == null) {
            spanned = markwon.render(node);
            cache.put(node, spanned);
        }
        markwon.setParsedMarkdown(holder.textView, spanned);
    }

    @Override
    public long id(@NonNull Node node) {
        return node.hashCode();
    }

    @Override
    public void clear() {
        cache.clear();
    }

    public static class Holder extends MarkwonAdapter.Holder {

        final TextView textView;

        protected Holder(@NonNull View itemView) {
            super(itemView);

            this.textView = requireView(R.id.text);
            this.textView.setSpannableFactory(NO_COPY_SPANNABLE_FACTORY);
        }
    }

    private static class NoCopySpannableFactory extends Spannable.Factory {

        @Override
        public Spannable newSpannable(CharSequence source) {
            return source instanceof Spannable
                    ? (Spannable) source
                    : new SpannableString(source);
        }
    }
}
