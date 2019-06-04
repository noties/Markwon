package io.noties.markwon;

import android.support.annotation.NonNull;

import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.core.spans.LinkSpan;
import io.noties.markwon.html.MarkwonHtmlParser;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.image.AsyncDrawableLoader;
import io.noties.markwon.image.ImageSizeResolver;
import io.noties.markwon.image.ImageSizeResolverDef;
import io.noties.markwon.syntax.SyntaxHighlight;
import io.noties.markwon.syntax.SyntaxHighlightNoOp;
import io.noties.markwon.urlprocessor.UrlProcessor;
import io.noties.markwon.urlprocessor.UrlProcessorNoOp;

/**
 * since 3.0.0 renamed `SpannableConfiguration` -&gt; `MarkwonConfiguration`
 */
@SuppressWarnings("WeakerAccess")
public class MarkwonConfiguration {

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    private final MarkwonTheme theme;
    private final AsyncDrawableLoader asyncDrawableLoader;
    private final SyntaxHighlight syntaxHighlight;
    private final LinkSpan.Resolver linkResolver;
    private final UrlProcessor urlProcessor;
    private final ImageSizeResolver imageSizeResolver;
    private final MarkwonHtmlParser htmlParser;
    private final MarkwonHtmlRenderer htmlRenderer;

    // @since 3.0.0
    private final MarkwonSpansFactory spansFactory;

    private MarkwonConfiguration(@NonNull Builder builder) {
        this.theme = builder.theme;
        this.asyncDrawableLoader = builder.asyncDrawableLoader;
        this.syntaxHighlight = builder.syntaxHighlight;
        this.linkResolver = builder.linkResolver;
        this.urlProcessor = builder.urlProcessor;
        this.imageSizeResolver = builder.imageSizeResolver;
        this.spansFactory = builder.spansFactory;
        this.htmlParser = builder.htmlParser;
        this.htmlRenderer = builder.htmlRenderer;
    }

    @NonNull
    public MarkwonTheme theme() {
        return theme;
    }

    @NonNull
    public AsyncDrawableLoader asyncDrawableLoader() {
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
    public MarkwonHtmlParser htmlParser() {
        return htmlParser;
    }

    @NonNull
    public MarkwonHtmlRenderer htmlRenderer() {
        return htmlRenderer;
    }

    /**
     * @since 3.0.0
     */
    @NonNull
    public MarkwonSpansFactory spansFactory() {
        return spansFactory;
    }

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public static class Builder {

        private MarkwonTheme theme;
        private AsyncDrawableLoader asyncDrawableLoader;
        private SyntaxHighlight syntaxHighlight;
        private LinkSpan.Resolver linkResolver;
        private UrlProcessor urlProcessor;
        private ImageSizeResolver imageSizeResolver;
        private MarkwonHtmlParser htmlParser;
        private MarkwonHtmlRenderer htmlRenderer;
        private MarkwonSpansFactory spansFactory;

        Builder() {
        }

        /**
         * @since 4.0.0-SNAPSHOT
         */
        @NonNull
        public Builder asyncDrawableLoader(@NonNull AsyncDrawableLoader asyncDrawableLoader) {
            this.asyncDrawableLoader = asyncDrawableLoader;
            return this;
        }

        /**
         * @since 4.0.0-SNAPSHOT
         */
        @NonNull
        public Builder htmlRenderer(@NonNull MarkwonHtmlRenderer htmlRenderer) {
            this.htmlRenderer = htmlRenderer;
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
        public Builder htmlParser(@NonNull MarkwonHtmlParser htmlParser) {
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

        @NonNull
        public MarkwonConfiguration build(
                @NonNull MarkwonTheme theme,
                @NonNull MarkwonSpansFactory spansFactory) {

            this.theme = theme;
            this.spansFactory = spansFactory;

            // @since 4.0.0-SNAPSHOT
            if (asyncDrawableLoader == null) {
                asyncDrawableLoader = AsyncDrawableLoader.noOp();
            }

            // @since 4.0.0-SNAPSHOT
            if (htmlRenderer == null) {
                htmlRenderer = MarkwonHtmlRenderer.noOp();
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

            if (htmlParser == null) {
                htmlParser = MarkwonHtmlParser.noOp();
            }

            return new MarkwonConfiguration(this);
        }
    }

}
