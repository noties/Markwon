package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.widget.TextView;

import org.commonmark.node.Node;

public abstract class Markwon {

    /**
     * @since 3.0.0
     */
    @NonNull
    public static Builder builder(@NonNull Context context) {
        return new MarkwonBuilderImpl(context);
    }

    @NonNull
    public abstract Node parse(@NonNull String input);

    @NonNull
    public abstract Spanned render(@NonNull Node node);

    // parse + render
    @NonNull
    public abstract Spanned toMarkdown(@NonNull String input);

    public abstract void setMarkdown(@NonNull TextView textView, @NonNull String markdown);

    public abstract void setParsedMarkdown(@NonNull TextView textView, @NonNull CharSequence markdown);

    /**
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
