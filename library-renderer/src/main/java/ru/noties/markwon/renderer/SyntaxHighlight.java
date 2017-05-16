package ru.noties.markwon.renderer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface SyntaxHighlight {

    @NonNull
    CharSequence highlight(@Nullable String info, @NonNull String code);
}
