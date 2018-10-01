package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.html.api.HtmlTag;

public abstract class SimpleTagHandler extends TagHandler {

    @Nullable
    public abstract Object getSpans(@NonNull SpannableConfiguration configuration, @NonNull HtmlTag tag);

    @Override
    public void handle(@NonNull SpannableConfiguration configuration, @NonNull SpannableBuilder builder, @NonNull HtmlTag tag) {
        builder.setSpans(getSpans(configuration, tag), tag.start(), tag.end());
    }
}
