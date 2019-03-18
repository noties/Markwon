package ru.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.MarkwonVisitor;

class MarkwonHtmlRendererNoOp extends MarkwonHtmlRenderer {

    @Override
    public void render(@NonNull MarkwonVisitor visitor, @NonNull MarkwonHtmlParser parser) {
        parser.reset();
    }

    @Nullable
    @Override
    public TagHandler tagHandler(@NonNull String tagName) {
        return null;
    }
}
