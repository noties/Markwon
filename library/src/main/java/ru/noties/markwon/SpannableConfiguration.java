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

    private final SpannableTheme mTheme;
    private final AsyncDrawable.Loader mAsyncDrawableLoader;
    private final SyntaxHighlight mSyntaxHighlight;
    private final LinkSpan.Resolver mLinkResolver;
    private final UrlProcessor mUrlProcessor;
    private final SpannableHtmlParser mHtmlParser;
    private final ImageClickResolver mImageClickResolver;

    private SpannableConfiguration(Builder builder) {
        mTheme = builder.theme;
        mAsyncDrawableLoader = builder.asyncDrawableLoader;
        mSyntaxHighlight = builder.syntaxHighlight;
        mLinkResolver = builder.linkResolver;
        mUrlProcessor = builder.urlProcessor;
        mHtmlParser = builder.htmlParser;
        mImageClickResolver = builder.imageClickResolver;
    }

    public SpannableTheme theme() {
        return mTheme;
    }

    public AsyncDrawable.Loader asyncDrawableLoader() {
        return mAsyncDrawableLoader;
    }

    public SyntaxHighlight syntaxHighlight() {
        return mSyntaxHighlight;
    }

    public LinkSpan.Resolver linkResolver() {
        return mLinkResolver;
    }

    public UrlProcessor urlProcessor() {
        return mUrlProcessor;
    }

    public SpannableHtmlParser htmlParser() {
        return mHtmlParser;
    }

    public ImageClickResolver imageClickResolver() {
        return mImageClickResolver;
    }

    public static class Builder {

        private final Context context;
        private SpannableTheme theme;
        private AsyncDrawable.Loader asyncDrawableLoader;
        private SyntaxHighlight syntaxHighlight;
        private LinkSpan.Resolver linkResolver;
        private UrlProcessor urlProcessor;
        private SpannableHtmlParser htmlParser;
        private ImageClickResolver imageClickResolver;

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

        public Builder setImageClickResolver(ImageClickResolver imageClickResolver) {
            this.imageClickResolver = imageClickResolver;
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
            if (imageClickResolver == null) {
                imageClickResolver = new ImageClickResolverDef();
            }
            if (htmlParser == null) {
                htmlParser = SpannableHtmlParser.create(theme, asyncDrawableLoader, urlProcessor,
                        linkResolver, imageClickResolver);
            }
            return new SpannableConfiguration(this);
        }
    }

}
