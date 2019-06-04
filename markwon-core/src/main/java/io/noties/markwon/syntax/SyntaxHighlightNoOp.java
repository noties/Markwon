package io.noties.markwon.syntax;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SyntaxHighlightNoOp implements SyntaxHighlight {
    @NonNull
    @Override
    public CharSequence highlight(@Nullable String info, @NonNull String code) {
        return code;
    }
}
