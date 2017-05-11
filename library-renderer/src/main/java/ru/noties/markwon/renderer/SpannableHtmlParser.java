package ru.noties.markwon.renderer;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class SpannableHtmlParser {

    // we need to handle images independently (in order to parse alt, width, height, etc)

    // for simple tags without arguments
    // <b>, <i>, etc
    public interface SpanProvider {
        Object provide();
    }

    public interface HtmlParser {
        Object[] getSpans(@NonNull String html);
    }

    // creates default parser
    public static SpannableHtmlParser create() {
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Map<String, SpanProvider> customTags;
    private final HtmlParser parser;

    private SpannableHtmlParser(Builder builder) {
        this.customTags = builder.customTags;
        this.parser = builder.parser;
    }

    public static class Builder {

        private final Map<String, SpanProvider> customTags = new HashMap<>(3);
        private HtmlParser parser;

        public Builder customTag(@NonNull String tag, @NonNull SpanProvider provider) {
            customTags.put(tag, provider);
            return this;
        }

        public Builder setParser(@NonNull HtmlParser parser) {
            this.parser = parser;
            return this;
        }

        public SpannableHtmlParser build() {
            if (parser == null) {
                // todo, images....
                parser = DefaultHtmlParser.create(null, null);
            }
            return new SpannableHtmlParser(this);
        }
    }

    public static abstract class DefaultHtmlParser implements HtmlParser {

        public static DefaultHtmlParser create(@Nullable Html.ImageGetter imageGetter, @Nullable Html.TagHandler tagHandler) {
            final DefaultHtmlParser parser;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                parser = new Parser24(imageGetter, tagHandler);
            } else {
                parser = new ParserPre24(imageGetter, tagHandler);
            }
            return parser;
        }

        final Html.ImageGetter imageGetter;
        final Html.TagHandler tagHandler;

        DefaultHtmlParser(Html.ImageGetter imageGetter, Html.TagHandler tagHandler) {
            this.imageGetter = imageGetter;
            this.tagHandler = tagHandler;
        }

        Object[] getSpans(Spanned spanned) {
            final Object[] spans;
            final int length = spanned != null ? spanned.length() : 0;
            if (length == 0) {
                spans = null;
            } else {
                spans = spanned.getSpans(0, length, Object.class);
            }
            return spans;
        }

        @SuppressWarnings("deprecation")
        private static class ParserPre24 extends DefaultHtmlParser {

            ParserPre24(Html.ImageGetter imageGetter, Html.TagHandler tagHandler) {
                super(imageGetter, tagHandler);
            }

            @Override
            public Object[] getSpans(@NonNull String html) {
                return getSpans(Html.fromHtml(html, imageGetter, tagHandler));
            }
        }

        @TargetApi(Build.VERSION_CODES.N)
        private static class Parser24 extends DefaultHtmlParser {

            Parser24(Html.ImageGetter imageGetter, Html.TagHandler tagHandler) {
                super(imageGetter, tagHandler);
            }

            @Override
            public Object[] getSpans(@NonNull String html) {
                return getSpans(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT, imageGetter, tagHandler));
            }
        }
    }
}
