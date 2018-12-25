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

public class SimpleEntry implements MarkwonAdapter.Entry<SimpleEntry.Holder, Node> {

    private static final NoCopySpannableFactory FACTORY = new NoCopySpannableFactory();

    // small cache, maybe add pre-compute of text, also spannableFactory (so no copying of spans)
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

    static class Holder extends MarkwonAdapter.Holder {

        final TextView textView;

        Holder(@NonNull View itemView) {
            super(itemView);

            this.textView = requireView(R.id.text);
            this.textView.setSpannableFactory(FACTORY);
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
