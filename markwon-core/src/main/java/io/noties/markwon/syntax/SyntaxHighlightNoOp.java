package io.noties.markwon.syntax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SyntaxHighlightNoOp implements SyntaxHighlight {
    @NonNull
    @Override
    public CharSequence highlight(@Nullable String info, @NonNull String code) {
        return code;
    }
}
