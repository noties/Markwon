package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import org.commonmark.node.Node;

public abstract class Markwon {

    @NonNull
    public static Builder builder(@NonNull Context context) {
        return new MarkwonBuilderImpl(context);
    }

    @NonNull
    public abstract Node parse(@NonNull String input);

    @NonNull
    public abstract CharSequence render(@NonNull Node node);

    // parse + render
    @NonNull
    public abstract CharSequence toMarkdown(@NonNull String input);

    public abstract void setMarkdown(@NonNull TextView textView, @NonNull String markdown);

    public abstract void setParsedMarkdown(@NonNull TextView textView, @NonNull CharSequence markdown);

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
        Builder use(@NonNull MarkwonPlugin plugin);

        @NonNull
        Markwon build();
    }
}
