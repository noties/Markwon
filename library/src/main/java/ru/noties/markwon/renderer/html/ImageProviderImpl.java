package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.Map;

import ru.noties.markwon.UrlProcessor;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.AsyncDrawableSpan;
import ru.noties.markwon.spans.SpannableTheme;

class ImageProviderImpl implements SpannableHtmlParser.ImageProvider {

    private final SpannableTheme theme;
    private final AsyncDrawable.Loader loader;
    private final UrlProcessor urlProcessor;

    ImageProviderImpl(
            @NonNull SpannableTheme theme,
            @NonNull AsyncDrawable.Loader loader,
            @NonNull UrlProcessor urlProcessor) {
        this.theme = theme;
        this.loader = loader;
        this.urlProcessor = urlProcessor;
    }

    @Override
    public Spanned provide(@NonNull SpannableHtmlParser.Tag tag) {

        final Spanned spanned;

        final Map<String, String> attributes = tag.attributes();
        final String src = attributes.get("src");
        final String alt = attributes.get("alt");

        if (!TextUtils.isEmpty(src)) {

            final String destination = urlProcessor.process(src);

            final String replacement;
            if (!TextUtils.isEmpty(alt)) {
                replacement = alt;
            } else {
                replacement = "\uFFFC";
            }

            final AsyncDrawable drawable = new AsyncDrawable(destination, loader);
            final AsyncDrawableSpan span = new AsyncDrawableSpan(theme, drawable);

            final SpannableString string = new SpannableString(replacement);
            string.setSpan(span, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            spanned = string;
        } else {
            spanned = null;
        }

        return spanned;
    }
}
