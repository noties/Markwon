package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;

import org.commonmark.node.Node;

public abstract class Markwon2 {

    @NonNull
    public static Builder builder(@NonNull Context context) {
        return new MarkwonBuilderImpl(context);
    }

    @NonNull
    public abstract Node parse(@NonNull String input);

    @NonNull
    public abstract CharSequence render(@NonNull Node node);

    @NonNull
    public abstract CharSequence markdown(@NonNull String input);

    public interface Builder {

        @NonNull
        Builder use(@NonNull MarkwonPlugin plugin);

        @NonNull
        Markwon2 build();
    }
}
