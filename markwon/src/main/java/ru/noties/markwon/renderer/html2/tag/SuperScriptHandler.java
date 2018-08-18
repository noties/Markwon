package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.html.api.HtmlTag;

public class SuperScriptHandler implements TagHandler {
    @Nullable
    @Override
    public Object getSpans(@NonNull SpannableConfiguration configuration, @NonNull HtmlTag tag) {
        return configuration.factory().superScript(configuration.theme());
    }
}
