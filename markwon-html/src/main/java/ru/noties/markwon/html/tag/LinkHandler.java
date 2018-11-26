package ru.noties.markwon.html.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.html.HtmlTag;

public class LinkHandler extends SimpleTagHandler {
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull HtmlTag tag) {
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
