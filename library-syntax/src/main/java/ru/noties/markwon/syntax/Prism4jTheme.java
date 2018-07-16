package ru.noties.markwon.syntax;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.prism4j.Prism4j;

public interface Prism4jTheme {

    @ColorInt
    int background();

    void apply(
            @NonNull String language,
            @NonNull Prism4j.Syntax syntax,
            @NonNull SpannableBuilder builder,
            int start,
            int end
    );
}
