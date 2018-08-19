package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.noties.markwon.html.api.HtmlTag;
import ru.noties.markwon.html.api.MarkwonHtmlParser;
import ru.noties.markwon.renderer.ImageSizeResolver;
import ru.noties.markwon.renderer.ImageSizeResolverDef;
import ru.noties.markwon.renderer.html2.MarkwonHtmlRenderer;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.SpannableTheme;

@SuppressWarnings("WeakerAccess")
public class SpannableConfiguration {

    // creates default configuration
    @NonNull
    public static SpannableConfiguration create(@NonNull Context context) {
        return new Builder(context).build();
    }

    @NonNull
    public static Builder builder(@NonNull Context context) {
        return new Builder(context);
    }

    private final SpannableTheme theme;
    private final AsyncDrawable.Loader asyncDrawableLoader;
    private final SyntaxHighlight syntaxHighlight;
    private final LinkSpan.Resolver linkResolver;
    private final UrlProcessor urlProcessor;
    private final ImageSizeResolver imageSizeResolver;
    private final SpannableFactory factory; // @since 1.1.0
    private final boolean softBreakAddsNewLine; // @since 1.1.1
    private final boolean trimWhiteSpaceEnd; // @since 2.0.0
    private final MarkwonHtmlParser htmlParser; // @since 2.0.0
    private final MarkwonHtmlRenderer htmlRenderer; // @since 2.0.0
    private final boolean htmlIgnoreNonClosedTags; // @since 2.0.0

    private SpannableConfiguration(@NonNull Builder builder) {
        this.theme = builder.theme;
        this.asyncDrawableLoader = builder.asyncDrawableLoader;
        this.syntaxHighlight = builder.syntaxHighlight;
        this.linkResolver = builder.linkResolver;
        this.urlProcessor = builder.urlProcessor;
        this.imageSizeResolver = builder.imageSizeResolver;
        this.factory = builder.factory;
        this.softBreakAddsNewLine = builder.softBreakAddsNewLine;
        this.trimWhiteSpaceEnd = builder.trimWhiteSpaceEnd;
        this.htmlParser = builder.htmlParser;
        this.htmlRenderer = builder.htmlRenderer;
        this.htmlIgnoreNonClosedTags = builder.htmlIgnoreNonClosedTags;
    }

    @NonNull
    public SpannableTheme theme() {
        return theme;
    }

    @NonNull
    public AsyncDrawable.Loader asyncDrawableLoader() {
        return asyncDrawableLoader;
    }

    @NonNull
    public SyntaxHighlight syntaxHighlight() {
        return syntaxHighlight;
    }

    @NonNull
    public LinkSpan.Resolver linkResolver() {
        return linkResolver;
    }

    @NonNull
    public UrlProcessor urlProcessor() {
        return urlProcessor;
    }

    @NonNull
    public ImageSizeResolver imageSizeResolver() {
        return imageSizeResolver;
    }

    @NonNull
    public SpannableFactory factory() {
        return factory;
    }

    /**
     * @return a flag indicating if soft break should be treated as a hard
     * break and thus adding a new line instead of adding a white space
     * @since 1.1.1
     */
    public boolean softBreakAddsNewLine() {
        return softBreakAddsNewLine;
    }

    /**
     * @since 2.0.0
     */
    public boolean trimWhiteSpaceEnd() {
        return trimWhiteSpaceEnd;
    }

    /**
     * @since 2.0.0
     */
    @NonNull
    public MarkwonHtmlParser htmlParser() {
        return htmlParser;
    }

    /**
     * @since 2.0.0
     */
    @NonNull
    public MarkwonHtmlRenderer htmlRenderer() {
        return htmlRenderer;
    }

    /**
     * @since 2.0.0
     */
    public boolean htmlIgnoreNonClosedTags() {
        return htmlIgnoreNonClosedTags;
    }

    @SuppressWarnings("unused")
    public static class Builder {

        private final Context context;
        private SpannableTheme theme;
        private AsyncDrawable.Loader asyncDrawableLoader;
        private SyntaxHighlight syntaxHighlight;
        private LinkSpan.Resolver linkResolver;
        private UrlProcessor urlProcessor;
        private ImageSizeResolver imageSizeResolver;
        private SpannableFactory factory; // @since 1.1.0
        private boolean softBreakAddsNewLine; // @since 1.1.1
        private boolean trimWhiteSpaceEnd = true; // @since 2.0.0
        private MarkwonHtmlParser htmlParser; // @since 2.0.0
        private MarkwonHtmlRenderer htmlRenderer; // @since 2.0.0
        private boolean htmlIgnoreNonClosedTags = true; // @since 2.0.0

        Builder(@NonNull Context context) {
            this.context = context;
        }

        @NonNull
        public Builder theme(@NonNull SpannableTheme theme) {
            this.theme = theme;
            return this;
        }

        @NonNull
        public Builder asyncDrawableLoader(@NonNull AsyncDrawable.Loader asyncDrawableLoader) {
            this.asyncDrawableLoader = asyncDrawableLoader;
            return this;
        }

        @NonNull
        public Builder syntaxHighlight(@NonNull SyntaxHighlight syntaxHighlight) {
            this.syntaxHighlight = syntaxHighlight;
            return this;
        }

        @NonNull
        public Builder linkResolver(@NonNull LinkSpan.Resolver linkResolver) {
            this.linkResolver = linkResolver;
            return this;
        }

        @NonNull
        public Builder urlProcessor(@NonNull UrlProcessor urlProcessor) {
            this.urlProcessor = urlProcessor;
            return this;
        }

        /**
         * @since 1.0.1
         */
        @NonNull
        public Builder imageSizeResolver(@NonNull ImageSizeResolver imageSizeResolver) {
            this.imageSizeResolver = imageSizeResolver;
            return this;
        }

        /**
         * @since 1.1.0
         */
        @NonNull
        public Builder factory(@NonNull SpannableFactory factory) {
            this.factory = factory;
            return this;
        }

        /**
         * @param softBreakAddsNewLine a flag indicating if soft break should be treated as a hard
         *                             break and thus adding a new line instead of adding a white space
         * @return self
         * @see <a href="https://spec.commonmark.org/0.28/#soft-line-breaks" > spec </a >
         * @since 1.1.1
         */
        @NonNull
        public Builder softBreakAddsNewLine(boolean softBreakAddsNewLine) {
            this.softBreakAddsNewLine = softBreakAddsNewLine;
            return this;
        }

        /**
         * Will trim white space(s) from the end from resulting text.
         * By default `true`
         *
         * @since 2.0.0
         */
        @NonNull
        public Builder trimWhiteSpaceEnd(boolean trimWhiteSpaceEnd) {
            this.trimWhiteSpaceEnd = trimWhiteSpaceEnd;
            return this;
        }

        /**
         * @since 2.0.0
         */
        @NonNull
        public Builder htmlParser(@NonNull MarkwonHtmlParser htmlParser) {
            this.htmlParser = htmlParser;
            return this;
        }

        /**
         * @since 2.0.0
         */
        @NonNull
        public Builder htmlRenderer(@NonNull MarkwonHtmlRenderer htmlRenderer) {
            this.htmlRenderer = htmlRenderer;
            return this;
        }

        /**
         * @param htmlIgnoreNonClosedTags that indicates if non-closed html tags should be kept open.
         *                                If this argument is false then all non-closed HTML tags
         *                                will be closed at the end of a document. Otherwise they will
         *                                be delivered non-closed {@link HtmlTag#isClosed()}
         * @since 2.0.0
         */
        @NonNull
        public Builder htmlIgnoreNonClosedTags(boolean htmlIgnoreNonClosedTags) {
            this.htmlIgnoreNonClosedTags = htmlIgnoreNonClosedTags;
            return this;
        }

        @NonNull
        public SpannableConfiguration build() {

            if (theme == null) {
                theme = SpannableTheme.create(context);
            }

            if (asyncDrawableLoader == null) {
                asyncDrawableLoader = new AsyncDrawableLoaderNoOp();
            }

            if (syntaxHighlight == null) {
                syntaxHighlight = new SyntaxHighlightNoOp();
            }

            if (linkResolver == null) {
                linkResolver = new LinkResolverDef();
            }

            if (urlProcessor == null) {
                urlProcessor = new UrlProcessorNoOp();
            }

            if (imageSizeResolver == null) {
                imageSizeResolver = new ImageSizeResolverDef();
            }

            // @since 1.1.0
            if (factory == null) {
                factory = SpannableFactoryDef.create();
            }

            // @since 2.0.0
            if (htmlParser == null) {
                try {
                    // if impl artifact was excluded -> fallback to no-op implementation
                    htmlParser = ru.noties.markwon.html.impl.MarkwonHtmlParserImpl.create();
                } catch (Throwable t) {
                    htmlParser = MarkwonHtmlParser.noOp();
                }
            }

            // @since 2.0.0
            if (htmlRenderer == null) {
                htmlRenderer = MarkwonHtmlRenderer.create();
            }

            return new SpannableConfiguration(this);
        }
    }

}
