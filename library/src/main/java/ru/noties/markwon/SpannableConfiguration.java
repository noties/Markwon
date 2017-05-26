package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.noties.markwon.renderer.html.SpannableHtmlParser;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.SpannableTheme;

@SuppressWarnings("WeakerAccess")
public class SpannableConfiguration {

    // creates default configuration
    public static SpannableConfiguration create(@NonNull Context context) {
        return new Builder(context).build();
    }

    public static Builder builder(@NonNull Context context) {
        return new Builder(context);
    }

    private final SpannableTheme theme;
    private final AsyncDrawable.Loader asyncDrawableLoader;
    private final SyntaxHighlight syntaxHighlight;
    private final LinkSpan.Resolver linkResolver;
    private final UrlProcessor urlProcessor;
    private final SpannableHtmlParser htmlParser;

    private SpannableConfiguration(Builder builder) {
        this.theme = builder.theme;
        this.asyncDrawableLoader = builder.asyncDrawableLoader;
        this.syntaxHighlight = builder.syntaxHighlight;
        this.linkResolver = builder.linkResolver;
        this.urlProcessor = builder.urlProcessor;
        this.htmlParser = builder.htmlParser;
    }

    public SpannableTheme theme() {
        return theme;
    }

    public AsyncDrawable.Loader asyncDrawableLoader() {
        return asyncDrawableLoader;
    }

    public SyntaxHighlight syntaxHighlight() {
        return syntaxHighlight;
    }

    public LinkSpan.Resolver linkResolver() {
        return linkResolver;
    }

    public UrlProcessor urlProcessor() {
        return urlProcessor;
    }

    public SpannableHtmlParser htmlParser() {
        return htmlParser;
    }

    public static class Builder {

        private final Context context;
        private SpannableTheme theme;
        private AsyncDrawable.Loader asyncDrawableLoader;
        private SyntaxHighlight syntaxHighlight;
        private LinkSpan.Resolver linkResolver;
        private UrlProcessor urlProcessor;
        private SpannableHtmlParser htmlParser;

        Builder(Context context) {
            this.context = context;
        }

        public Builder theme(SpannableTheme theme) {
            this.theme = theme;
            return this;
        }

        public Builder asyncDrawableLoader(AsyncDrawable.Loader asyncDrawableLoader) {
            this.asyncDrawableLoader = asyncDrawableLoader;
            return this;
        }

        public Builder syntaxHighlight(SyntaxHighlight syntaxHighlight) {
            this.syntaxHighlight = syntaxHighlight;
            return this;
        }

        public Builder linkResolver(LinkSpan.Resolver linkResolver) {
            this.linkResolver = linkResolver;
            return this;
        }

        public Builder urlProcessor(UrlProcessor urlProcessor) {
            this.urlProcessor = urlProcessor;
            return this;
        }

        public Builder htmlParser(SpannableHtmlParser htmlParser) {
            this.htmlParser = htmlParser;
            return this;
        }

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
            if (htmlParser == null) {
                htmlParser = SpannableHtmlParser.create(theme, asyncDrawableLoader, urlProcessor, linkResolver);
            }
            return new SpannableConfiguration(this);
        }
    }

}
