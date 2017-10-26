package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Map;

import ru.noties.markwon.UrlProcessor;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.SpannableTheme;

class LinkProvider implements SpannableHtmlParser.SpanProvider {

    private final SpannableTheme mTheme;
    private final UrlProcessor mUrlProcessor;
    private final LinkSpan.Resolver mResolver;

    LinkProvider(
            @NonNull SpannableTheme theme,
            @NonNull UrlProcessor urlProcessor,
            @NonNull LinkSpan.Resolver resolver) {
        mTheme = theme;
        mUrlProcessor = urlProcessor;
        mResolver = resolver;
    }

    @Override
    public Object provide(@NonNull SpannableHtmlParser.Tag tag) {

        final Object span;

        final Map<String, String> attributes = tag.attributes();
        final String href = attributes.get("href");
        if (!TextUtils.isEmpty(href)) {

            final String destination = mUrlProcessor.process(href);
            span = new LinkSpan(mTheme, destination, mResolver);

        } else {
            span = null;
        }

        return span;
    }
}
