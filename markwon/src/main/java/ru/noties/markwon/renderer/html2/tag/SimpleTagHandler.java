package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.Spanned;

import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.html.api.HtmlTag;

public abstract class SimpleTagHandler extends TagHandler {

    @Nullable
    public abstract Object getSpans(@NonNull SpannableConfiguration configuration, @NonNull HtmlTag tag);

    @Override
    public void handle(@NonNull SpannableConfiguration configuration,
                       @NonNull Spannable spannable,
                       @NonNull HtmlTag tag) {
        final Object spans = getSpans(configuration, tag);
        if (spans != null) {
            spannable.setSpan(spans, tag.start(), tag.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
