package ru.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This class will be used to append some text to output in order to
 * apply a Span for this tag. Please note that this class will be used for
 * _void_ tags and tags that are self-closed (even if HTML spec doesn\'t specify
 * a tag as self-closed). This is due to the fact that underlying parser does not
 * validate context and does not check if a tag is correctly used. Plus it will be
 * used for tags without content, for example: {@code <my-custom-element></my-custom-element>}
 *
 * @since 2.0.0
 */
public class HtmlEmptyTagReplacement {

    @NonNull
    public static HtmlEmptyTagReplacement create() {
        return new HtmlEmptyTagReplacement();
    }

    private static final String IMG_REPLACEMENT = "\uFFFC";

    /**
     * @return replacement for supplied startTag or null if no replacement should occur (which will
     * lead to `Inline` tag have start &amp; end the same value, thus not applicable for applying a Span)
     */
    @Nullable
    public String replace(@NonNull HtmlTag tag) {

        final String replacement;

        final String name = tag.name();

        if ("br".equals(name)) {
            replacement = "\n";
        } else if ("img".equals(name)) {
            final String alt = tag.attributes().get("alt");
            if (alt == null
                    || alt.length() == 0) {
                // no alt is provided
                replacement = IMG_REPLACEMENT;
            } else {
                replacement = alt;
            }
        } else {
            replacement = null;
        }

        return replacement;
    }
}
