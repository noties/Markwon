package io.noties.markwon.sample.html;

import android.text.TextUtils;
import android.text.style.TypefaceSpan;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;

import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.html.TagHandler;

public class HtmlFontTagHandler extends TagHandler {

    @Override
    public void handle(@NonNull MarkwonVisitor visitor, @NonNull MarkwonHtmlRenderer renderer, @NonNull HtmlTag tag) {

        if (tag.isBlock()) {
            visitChildren(visitor, renderer, tag.getAsBlock());
        }

        final String font = tag.attributes().get("name");
        if (!TextUtils.isEmpty(font)) {
            SpannableBuilder.setSpans(
                    visitor.builder(),
                    new TypefaceSpan(font),
                    tag.start(),
                    tag.end()
            );
        }
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Collections.singleton("font");
    }
}
