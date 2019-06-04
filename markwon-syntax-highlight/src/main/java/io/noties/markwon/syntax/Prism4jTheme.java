package io.noties.markwon.syntax;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;

import ru.noties.prism4j.Prism4j;

public interface Prism4jTheme {

    @ColorInt
    int background();

    @ColorInt
    int textColor();

    void apply(
            @NonNull String language,
            @NonNull Prism4j.Syntax syntax,
            @NonNull SpannableStringBuilder builder,
            int start,
            int end
    );
}
