package ru.noties.markwon.renderer.html;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.noties.debug.Debug;
import ru.noties.markwon.UrlProcessor;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.SpannableTheme;

@SuppressWarnings("WeakerAccess")
public class SpannableHtmlParser {

    // we need to handle images independently (in order to parse alt, width, height, etc)

    // creates default parser
    public static SpannableHtmlParser create(
            @NonNull SpannableTheme theme,
            @NonNull AsyncDrawable.Loader loader
    ) {
        return builderWithDefaults(theme, loader, null)
                .build();
    }

    public static SpannableHtmlParser create(
            @NonNull SpannableTheme theme,
            @NonNull AsyncDrawable.Loader loader,
            @NonNull UrlProcessor urlProcessor
    ) {
        return builderWithDefaults(theme, loader, urlProcessor)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderWithDefaults(
            @NonNull SpannableTheme theme,
            @Nullable AsyncDrawable.Loader asyncDrawableLoader,
            @Nullable UrlProcessor urlProcessor
    ) {

        final BoldProvider boldProvider = new BoldProvider();
        final ItalicsProvider italicsProvider = new ItalicsProvider();
        final StrikeProvider strikeProvider = new StrikeProvider();

        final HtmlParser parser;
        if (asyncDrawableLoader != null) {
            parser = DefaultHtmlParser.create(new HtmlImageGetter(asyncDrawableLoader, urlProcessor), null);
        } else {
            parser = DefaultHtmlParser.create(null, null);
        }

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
                .customTag("strike", strikeProvider)
                .parser(parser);
    }

    // for simple tags without arguments
    // <b>, <i>, etc
    public interface SpanProvider {
        Object provide();
    }

    public interface HtmlParser {
        Object[] getSpans(@NonNull String html);

        Spanned parse(@NonNull String html);
    }

    private static final String LINK_START = "<a ";

    private final Map<String, SpanProvider> customTags;
    private final Set<String> voidTags;
    private final HtmlParser parser;

    private SpannableHtmlParser(Builder builder) {
        this.customTags = builder.customTags;
        this.voidTags = voidTags();
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
            // okay, we will consider a tag a void one if it's in our void list tag
            final boolean closing = '<' == html.charAt(0) && '/' == html.charAt(1);
            final boolean voidTag;
            if (closing) {
                voidTag = false;
            } else {
                int firstNonChar = -1;
                for (int i = 1; i < length; i++) {
                    if (!Character.isLetterOrDigit(html.charAt(i))) {
                        firstNonChar = i;
                        break;
                    }
                }
                if (firstNonChar > 1) {
                    final String name = html.substring(1, firstNonChar);
                    voidTag = voidTags.contains(name);
                } else {
                    voidTag = false;
                }
            }

            // todo, we do not strip to void tag name, so it can be possibly ended with `/`
            final String name = closing
                    ? html.substring(2, length - 1)
                    : html.substring(1, length - 1);

            tag = new Tag(name, !closing, voidTag);
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
        Debug.i("html: %s", html);
        return parser.getSpans(html);
    }

    // this is called when we encounter `void` tag
    // `img` is a void tag
    public Spanned html(String html) {
        Debug.i("html: %s", html);
        return parser.parse(html);
    }

    private static Set<String> voidTags() {
        final String[] tags = {
                "area", "base", "br", "col", "embed", "hr", "img", "input",
                "keygen", "link", "meta", "param", "source", "track", "wbr"
        };
        final Set<String> set = new HashSet<>(tags.length);
        Collections.addAll(set, tags);
        return set;
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
        private final boolean voidTag;

        public Tag(String name, boolean opening, boolean voidTag) {
            this.name = name;
            this.opening = opening;
            this.voidTag = voidTag;
        }

        public String name() {
            return name;
        }

        public boolean opening() {
            return opening;
        }

        public boolean voidTag() {
            return voidTag;
        }

        @Override
        public String toString() {
            return "Tag{" +
                    "name='" + name + '\'' +
                    ", opening=" + opening +
                    ", voidTag=" + voidTag +
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
                return getSpans(parse(html));
            }

            @Override
            public Spanned parse(@NonNull String html) {
                return Html.fromHtml(html, imageGetter, tagHandler);
            }
        }

        @TargetApi(Build.VERSION_CODES.N)
        private static class Parser24 extends DefaultHtmlParser {

            Parser24(Html.ImageGetter imageGetter, Html.TagHandler tagHandler) {
                super(imageGetter, tagHandler);
            }

            @Override
            public Object[] getSpans(@NonNull String html) {
                return getSpans(parse(html));
            }

            @Override
            public Spanned parse(@NonNull String html) {
                return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT, imageGetter, tagHandler);
            }
        }
    }
}
