package io.noties.markwon.recycler;

import android.graphics.Color;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import org.commonmark.node.Node;

import java.util.HashMap;
import java.util.Map;

import io.noties.markdown.boundarytext.RoundedBgTextView;
import io.noties.markwon.Markwon;
import io.noties.markwon.utils.NoCopySpannableFactory;

/**
 * @since 3.0.0
 */
@SuppressWarnings("WeakerAccess")
public class SimpleEntry extends MarkwonAdapter.Entry<Node, SimpleEntry.Holder> {

    /**
     * Create {@link SimpleEntry} that has TextView as the root view of
     * specified layout.
     */
    @NonNull
    public static SimpleEntry createTextViewIsRoot(@LayoutRes int layoutResId) {
        return new SimpleEntry(layoutResId, 0, Color.BLACK, "light");
    }

    @NonNull
    public static SimpleEntry create(@LayoutRes int layoutResId, @IdRes int textViewIdRes, @NonNull int textColor, @NonNull String theme) {
        return new SimpleEntry(layoutResId, textViewIdRes, textColor, theme);
    }

    // small cache for already rendered nodes
    private final Map<Node, Spanned> cache = new HashMap<>();

    private final int layoutResId;
    private final int textViewIdRes;
    private final int textColor;
    private String theme = "light";

    public SimpleEntry(@LayoutRes int layoutResId, @IdRes int textViewIdRes, @NonNull int textColor, String theme) {
        this.layoutResId = layoutResId;
        this.textViewIdRes = textViewIdRes;
        this.textColor = textColor;
        this.theme = theme;
    }

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(textViewIdRes, inflater.inflate(layoutResId, parent, false), textColor, theme);
    }

    @Override
    public void bindHolder(@NonNull Markwon markwon, @NonNull Holder holder, @NonNull Node node, int depth) {
        Spanned spanned = cache.get(node);
        if (spanned == null) {
            spanned = markwon.render(node);
            cache.put(node, spanned);
        }
        markwon.setParsedMarkdown(holder.textView, spanned);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    public static class Holder extends MarkwonAdapter.Holder {

        final RoundedBgTextView textView;

        protected Holder(@IdRes int textViewIdRes, @NonNull View itemView, @NonNull int textColor, String theme) {
            super(itemView);

            final RoundedBgTextView textView;
            if (textViewIdRes == 0) {
                if (!(itemView instanceof TextView)) {
                    throw new IllegalStateException("TextView is not root of layout " +
                            "(specify TextView ID explicitly): " + itemView);
                }
                textView = (RoundedBgTextView) itemView;
            } else {
                textView = requireView(textViewIdRes);
            }
            textView.setThemeChange(theme);
            textView.setTextColor(textColor);
            this.textView = textView;
            this.textView.setMovementMethod(new CustomMovementMethod());
            this.textView.setSpannableFactory(NoCopySpannableFactory.getInstance());
        }
    }
}