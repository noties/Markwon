package io.noties.markwon.syntax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public interface SyntaxHighlight {

    @NonNull
    CharSequence highlight(@Nullable String info, @NonNull String code);
}
