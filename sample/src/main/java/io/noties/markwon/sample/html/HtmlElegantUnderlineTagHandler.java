package io.noties.markwon.sample.html;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Collection;
import java.util.Collections;

import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.html.TagHandler;

@RequiresApi(Build.VERSION_CODES.KITKAT)
public class HtmlElegantUnderlineTagHandler extends TagHandler {

    @Override
    public void handle(@NonNull MarkwonVisitor visitor, @NonNull MarkwonHtmlRenderer renderer, @NonNull HtmlTag tag) {
        if (tag.isBlock()) {
            visitChildren(visitor, renderer, tag.getAsBlock());
        }
        SpannableBuilder.setSpans(
                visitor.builder(),
                ElegantUnderlineSpan.create(),
                tag.start(),
                tag.end()
        );
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Collections.singleton("u");
    }
}