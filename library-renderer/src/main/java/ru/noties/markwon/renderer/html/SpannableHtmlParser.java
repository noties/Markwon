package ru.noties.markwon.renderer.html;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;

import java.util.HashMap;
import java.util.Map;

import ru.noties.markwon.spans.SpannableTheme;

@SuppressWarnings("WeakerAccess")
public class SpannableHtmlParser {

    // we need to handle images independently (in order to parse alt, width, height, etc)

    // creates default parser
    public static SpannableHtmlParser create(@NonNull SpannableTheme theme) {
        return builderWithDefaults(theme)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderWithDefaults(@NonNull SpannableTheme theme) {

        final BoldProvider boldProvider = new BoldProvider();
        final ItalicsProvider italicsProvider = new ItalicsProvider();
        final StrikeProvider strikeProvider = new StrikeProvider();

        return new Builder()
                .customTag("b", boldProvider)
                .customTag("strong", boldProvider)
                .customTag("i", italicsProvider)
                .customTag("em", italicsProvider)
                .customTag("cite", italicsProvider)
                .customTag("dfn", italicsProvider)
                .customTag("sup", new SuperScriptProvider(theme))
                .customTag("sub", new SubScriptProvider(theme))
                .customTag("u", new UnderlineProvider())
                .customTag("del", strikeProvider)
                .customTag("s", strikeProvider)
                .customTag("strike", strikeProvider);
    }

    // for simple tags without arguments
    // <b>, <i>, etc
    public interface SpanProvider {
        Object provide();
    }

    public interface HtmlParser {
        Object[] getSpans(@NonNull String html);
    }

    private final Map<String, SpanProvider> customTags;
    private final HtmlParser parser;

    private SpannableHtmlParser(Builder builder) {
        this.customTags = builder.customTags;
        this.parser = builder.parser;
    }

    @Nullable
    public Tag parseTag(String html) {

        final Tag tag;

        final int length = html != null
                ? html.length()
                : 0;

        // absolutely minimum (`<i>`)
        if (length < 3) {
            tag = null;
        } else {
            final boolean closing = '<' == html.charAt(0) && '/' == html.charAt(1);
            final String name = closing
                    ? html.substring(2, length - 1)
                    : html.substring(1, length - 1);
            tag = new Tag(name, !closing);
        }

        return tag;
    }

    @Nullable
    public Object handleTag(String tag) {
        final Object out;
        final SpanProvider provider = customTags.get(tag);
        if (provider != null) {
            out = provider.provide();
        } else {
            out = null;
        }
        return out;
    }

    @Nullable
    public Object[] htmlSpans(String html) {
        // todo, additional handling of: image & link
        return parser.getSpans(html);
    }

    public static class Builder {

        private final Map<String, SpanProvider> customTags = new HashMap<>(3);
        private HtmlParser parser;

        public Builder customTag(@NonNull String tag, @NonNull SpanProvider provider) {
            customTags.put(tag, provider);
            return this;
        }

        public Builder parser(@NonNull HtmlParser parser) {
            this.parser = parser;
            return this;
        }

        public SpannableHtmlParser build() {
            if (parser == null) {
                parser = DefaultHtmlParser.create(null, null);
            }
            return new SpannableHtmlParser(this);
        }
    }

    public static class Tag {

        private final String name;
        private final boolean opening;

        public Tag(String name, boolean opening) {
            this.name = name;
            this.opening = opening;
        }

        public String name() {
            return name;
        }

        public boolean opening() {
            return opening;
        }

        @Override
        public String toString() {
            return "Tag{" +
                    "name='" + name + '\'' +
                    ", opening=" + opening +
                    '}';
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
