package io.noties.markwon.sample.html;

import android.text.Layout;
import android.text.style.AlignmentSpan;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;

import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.html.TagHandler;

class CenterTagHandler extends TagHandler {

    @Override
    public void handle(@NonNull MarkwonVisitor visitor, @NonNull MarkwonHtmlRenderer renderer, @NonNull HtmlTag tag) {
        Log.e("HTML", String.format("center, isBlock: %s", tag.isBlock()));
        if (tag.isBlock()) {
            visitChildren(visitor, renderer, tag.getAsBlock());
        }
        SpannableBuilder.setSpans(
                visitor.builder(),
                new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                tag.start(),
                tag.end()
        );
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Collections.singleton("center");
    }
}
