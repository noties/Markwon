package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.widget.TextView;

import org.commonmark.node.Node;

/**
 * Class to parse and render markdown. Since version 3.0.0 instance specific (previously consisted
 * of static stateless methods). An instance of builder can be obtained via {@link #builder(Context)}
 * method.
 *
 * @see #builder(Context)
 * @see Builder
 */
public abstract class Markwon {

    /**
     * Factory method to obtain an instance of {@link Builder}
     *
     * @see Builder
     * @since 3.0.0
     */
    @NonNull
    public static Builder builder(@NonNull Context context) {
        return new MarkwonBuilderImpl(context);
    }

    /**
     * Method to simply parse markdown (without rendering)
     *
     * @param input markdown input to parse
     * @return parsed via commonmark-java <code>org.commonmark.node.Node</code>
     * @see #render(Node)
     * @since 3.0.0
     */
    @NonNull
    public abstract Node parse(@NonNull String input);

    @NonNull
    public abstract Spanned render(@NonNull Node node);

    // parse + render
    @NonNull
    public abstract Spanned toMarkdown(@NonNull String input);

    public abstract void setMarkdown(@NonNull TextView textView, @NonNull String markdown);

    public abstract void setParsedMarkdown(@NonNull TextView textView, @NonNull Spanned markdown);

    /**
     * Builder for {@link Markwon}.
     * <p>
     * Please note that the order in which plugins are supplied is important as this order will be
     * used through the whole usage of built Markwon instance
     *
     * @since 3.0.0
     */
    public interface Builder {

        /**
         * Specify bufferType when applying text to a TextView {@code textView.setText(CharSequence,BufferType)}.
         * By default `BufferType.SPANNABLE` is used
         *
         * @param bufferType BufferType
         */
        @NonNull
        Builder bufferType(@NonNull TextView.BufferType bufferType);

        @NonNull
        Builder usePlugin(@NonNull MarkwonPlugin plugin);

        @NonNull
        Builder usePlugins(@NonNull Iterable<? extends MarkwonPlugin> plugins);

        @NonNull
        Markwon build();
    }
}
