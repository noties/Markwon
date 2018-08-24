package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.html.api.HtmlTag;

public class LinkHandler extends SimpleTagHandler {
    @Nullable
    @Override
    public Object getSpans(@NonNull SpannableConfiguration configuration, @NonNull HtmlTag tag) {
        final String destination = tag.attributes().get("href");
        if (!TextUtils.isEmpty(destination)) {
            return configuration.factory().link(
                    configuration.theme(),
                    configuration.urlProcessor().process(destination),
                    configuration.linkResolver()
            );
        }
        return null;
    }
}
