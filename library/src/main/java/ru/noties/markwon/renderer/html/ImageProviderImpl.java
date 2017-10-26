package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import java.util.Map;

import ru.noties.markwon.ImageClickResolver;
import ru.noties.markwon.UrlProcessor;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.AsyncDrawableSpan;
import ru.noties.markwon.spans.SpannableTheme;

class ImageProviderImpl implements SpannableHtmlParser.ImageProvider {

    private final SpannableTheme mTheme;
    private final AsyncDrawable.Loader mLoader;
    private final UrlProcessor mUrlProcessor;
    private final ImageClickResolver mImageClickResolver;

    ImageProviderImpl(
            @NonNull SpannableTheme theme,
            @NonNull AsyncDrawable.Loader loader,
            @NonNull UrlProcessor urlProcessor,
            @NonNull ImageClickResolver imageClickResolver) {
        mTheme = theme;
        mLoader = loader;
        mUrlProcessor = urlProcessor;
        mImageClickResolver = imageClickResolver;
    }

    @Override
    public Spanned provide(@NonNull SpannableHtmlParser.Tag tag) {

        final Spanned spanned;

        final Map<String, String> attributes = tag.attributes();
        final String src = attributes.get("src");
        final String alt = attributes.get("alt");

        if (!TextUtils.isEmpty(src)) {

            final String destination = mUrlProcessor.process(src);

            final String replacement;
            if (!TextUtils.isEmpty(alt)) {
                replacement = alt;
            } else {
                replacement = "\uFFFC";
            }

            final AsyncDrawable drawable = new AsyncDrawable(destination, mLoader);
            final AsyncDrawableSpan span = new AsyncDrawableSpan(mTheme, drawable);

            final SpannableString string = new SpannableString(replacement);
            string.setSpan(span, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            final ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    mImageClickResolver.resolve(view, destination);
                }
            };
            string.setSpan(clickableSpan, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            spanned = string;
        } else {
            spanned = null;
        }

        return spanned;
    }
}
