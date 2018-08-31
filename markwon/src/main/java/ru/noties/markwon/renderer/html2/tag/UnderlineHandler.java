package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.Spanned;

import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.html.api.HtmlTag;

public class UnderlineHandler extends TagHandler {

    @Override
    public void handle(
            @NonNull SpannableConfiguration configuration,
            @NonNull Spannable spannable,
            @NonNull HtmlTag tag) {

        // as parser doesn't treat U tag as an inline one,
        // thus doesn't allow children, we must visit them first

        if (tag.isBlock()) {
            visitChildren(configuration, spannable, tag.getAsBlock());
        }

        spannable.setSpan(configuration.factory().underline(),
                tag.start(), tag.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
