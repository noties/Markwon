package io.noties.markwon.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.MarkwonVisitor;

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
