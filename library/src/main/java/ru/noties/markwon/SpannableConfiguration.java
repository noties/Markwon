package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.noties.markwon.renderer.ImageSizeResolver;
import ru.noties.markwon.renderer.ImageSizeResolverDef;
import ru.noties.markwon.renderer.html.SpannableHtmlParser;
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
    private final SpannableHtmlParser htmlParser;
    private final ImageSizeResolver imageSizeResolver;
    private final SpannableFactory factory; // @since 1.1.0
    private final boolean softBreakAddsNewLine; // @since 1.1.1

    private SpannableConfiguration(@NonNull Builder builder) {
        this.theme = builder.theme;
        this.asyncDrawableLoader = builder.asyncDrawableLoader;
        this.syntaxHighlight = builder.syntaxHighlight;
        this.linkResolver = builder.linkResolver;
        this.urlProcessor = builder.urlProcessor;
        this.htmlParser = builder.htmlParser;
        this.imageSizeResolver = builder.imageSizeResolver;
        this.factory = builder.factory;
        this.softBreakAddsNewLine = builder.softBreakAddsNewLine;
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
    public SpannableHtmlParser htmlParser() {
        return htmlParser;
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

    @SuppressWarnings("unused")
    public static class Builder {

        private final Context context;
        private SpannableTheme theme;
        private AsyncDrawable.Loader asyncDrawableLoader;
        private SyntaxHighlight syntaxHighlight;
        private LinkSpan.Resolver linkResolver;
        private UrlProcessor urlProcessor;
        private SpannableHtmlParser htmlParser;
        private ImageSizeResolver imageSizeResolver;
        private SpannableFactory factory; // @since 1.1.0
        private boolean softBreakAddsNewLine; // @since 1.1.1

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

        @NonNull
        public Builder htmlParser(@NonNull SpannableHtmlParser htmlParser) {
            this.htmlParser = htmlParser;
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
         * @see <a href="https://spec.commonmark.org/0.28/#soft-line-breaks">spec</a>
         * @since 1.1.1
         */
        @NonNull
        public Builder softBreakAddsNewLine(boolean softBreakAddsNewLine) {
            this.softBreakAddsNewLine = softBreakAddsNewLine;
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

            if (htmlParser == null) {
                htmlParser = SpannableHtmlParser.create(
                        factory,
                        theme,
                        asyncDrawableLoader,
                        urlProcessor,
                        linkResolver,
                        imageSizeResolver);
            }

            return new SpannableConfiguration(this);
        }
    }

}
