package ru.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.SpannableBuilder;

class MarkwonHtmlRendererNoOp extends MarkwonHtmlRenderer {

    @Override
    public void render(@NonNull MarkwonConfiguration configuration, @NonNull SpannableBuilder builder, @NonNull MarkwonHtmlParser parser) {

    }

    @Nullable
    @Override
    public TagHandler tagHandler(@NonNull String tagName) {
        return null;
    }
}
