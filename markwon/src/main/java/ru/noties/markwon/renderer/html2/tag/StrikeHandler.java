package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.Spanned;

import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.html.api.HtmlTag;

public class StrikeHandler extends TagHandler {

    @Override
    public void handle(
            @NonNull SpannableConfiguration configuration,
            @NonNull Spannable spannable,
            @NonNull HtmlTag tag) {

        if (tag.isBlock()) {
            visitChildren(configuration, spannable, tag.getAsBlock());
        }

        spannable.setSpan(configuration.factory().strikethrough(), tag.start(), tag.end(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
