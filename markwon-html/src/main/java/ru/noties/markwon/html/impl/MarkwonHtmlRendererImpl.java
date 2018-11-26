package ru.noties.markwon.html.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.html.HtmlTag;
import ru.noties.markwon.html.MarkwonHtmlParser;
import ru.noties.markwon.html.MarkwonHtmlRenderer;
import ru.noties.markwon.html.TagHandler;
import ru.noties.markwon.html.impl.tag.BlockquoteHandler;
import ru.noties.markwon.html.impl.tag.EmphasisHandler;
import ru.noties.markwon.html.impl.tag.HeadingHandler;
import ru.noties.markwon.html.impl.tag.ImageHandler;
import ru.noties.markwon.html.impl.tag.LinkHandler;
import ru.noties.markwon.html.impl.tag.ListHandler;
import ru.noties.markwon.html.impl.tag.StrikeHandler;
import ru.noties.markwon.html.impl.tag.StrongEmphasisHandler;
import ru.noties.markwon.html.impl.tag.SubScriptHandler;
import ru.noties.markwon.html.impl.tag.SuperScriptHandler;
import ru.noties.markwon.html.impl.tag.UnderlineHandler;

public class MarkwonHtmlRendererImpl extends MarkwonHtmlRenderer {

    @NonNull
    public static MarkwonHtmlRendererImpl create() {
        return builderWithDefaults().build();
    }

    /**
     * @since 3.0.0
     */
    @NonNull
    public static MarkwonHtmlRendererImpl create(boolean allowNonClosedTags) {
        return builderWithDefaults(allowNonClosedTags).build();
    }

    @NonNull
    public static Builder builderWithDefaults() {
        return builderWithDefaults(false);
    }

    /**
     * @since 3.0.0
     */
    @NonNull
    public static Builder builderWithDefaults(boolean allowNonClosedTags) {

        final EmphasisHandler emphasisHandler = new EmphasisHandler();
        final StrongEmphasisHandler strongEmphasisHandler = new StrongEmphasisHandler();
        final StrikeHandler strikeHandler = new StrikeHandler();
        final UnderlineHandler underlineHandler = new UnderlineHandler();
        final ListHandler listHandler = new ListHandler();

        return builder()
                .allowNonClosedTags(allowNonClosedTags)
                .handler("i", emphasisHandler)
                .handler("em", emphasisHandler)
                .handler("cite", emphasisHandler)
                .handler("dfn", emphasisHandler)
                .handler("b", strongEmphasisHandler)
                .handler("strong", strongEmphasisHandler)
                .handler("sup", new SuperScriptHandler())
                .handler("sub", new SubScriptHandler())
                .handler("u", underlineHandler)
                .handler("ins", underlineHandler)
                .handler("del", strikeHandler)
                .handler("s", strikeHandler)
                .handler("strike", strikeHandler)
                .handler("a", new LinkHandler())
                .handler("ul", listHandler)
                .handler("ol", listHandler)
                .handler("img", ImageHandler.create())
                .handler("blockquote", new BlockquoteHandler())
                .handler("h1", new HeadingHandler(1))
                .handler("h2", new HeadingHandler(2))
                .handler("h3", new HeadingHandler(3))
                .handler("h4", new HeadingHandler(4))
                .handler("h5", new HeadingHandler(5))
                .handler("h6", new HeadingHandler(6));
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final float SCRIPT_DEF_TEXT_SIZE_RATIO = .75F;

    private final boolean allowNonClosedTags;
    private final Map<String, TagHandler> tagHandlers;

    private MarkwonHtmlRendererImpl(boolean allowNonClosedTags, @NonNull Map<String, TagHandler> tagHandlers) {
        this.allowNonClosedTags = allowNonClosedTags;
        this.tagHandlers = tagHandlers;
    }

    @Override
    public void render(
            @NonNull final MarkwonConfiguration configuration,
            @NonNull final SpannableBuilder builder,
            @NonNull MarkwonHtmlParser parser) {

        final int end;
        if (!allowNonClosedTags) {
            end = HtmlTag.NO_END;
        } else {
            end = builder.length();
        }

        parser.flushInlineTags(end, new MarkwonHtmlParser.FlushAction<HtmlTag.Inline>() {
            @Override
            public void apply(@NonNull List<HtmlTag.Inline> tags) {

                TagHandler handler;

                for (HtmlTag.Inline inline : tags) {

                    // if tag is not closed -> do not render
                    if (!inline.isClosed()) {
                        continue;
                    }

                    handler = tagHandler(inline.name());
                    if (handler != null) {
                        handler.handle(configuration, builder, inline);
                    }
                }
            }
        });

        parser.flushBlockTags(end, new MarkwonHtmlParser.FlushAction<HtmlTag.Block>() {
            @Override
            public void apply(@NonNull List<HtmlTag.Block> tags) {

                TagHandler handler;

                for (HtmlTag.Block block : tags) {

                    if (!block.isClosed()) {
                        continue;
                    }

                    handler = tagHandler(block.name());
                    if (handler != null) {
                        handler.handle(configuration, builder, block);
                    } else {
                        // see if any of children can be handled
                        apply(block.children());
                    }
                }
            }
        });

        parser.reset();
    }

    @Nullable
    @Override
    public TagHandler tagHandler(@NonNull String tagName) {
        return tagHandlers.get(tagName);
    }

    public static class Builder {

        private final Map<String, TagHandler> tagHandlers = new HashMap<>(2);
        private boolean allowNonClosedTags;

        @NonNull
        public Builder handler(@NonNull String tagName, @NonNull TagHandler tagHandler) {
            tagHandlers.put(tagName.toLowerCase(Locale.US), tagHandler);
            return this;
        }

        /**
         * @param allowNonClosedTags that indicates if non-closed html tags should be rendered.
         *                           If this argument is true then all non-closed HTML tags
         *                           will be closed at the end of a document. Otherwise they will
         *                           be delivered non-closed {@code HtmlTag#isClosed()} and thus not
         *                           rendered at all
         * @since 3.0.0
         */
        @NonNull
        public Builder allowNonClosedTags(boolean allowNonClosedTags) {
            this.allowNonClosedTags = allowNonClosedTags;
            return this;
        }

        @NonNull
        public MarkwonHtmlRendererImpl build() {
            return new MarkwonHtmlRendererImpl(allowNonClosedTags, Collections.unmodifiableMap(tagHandlers));
        }
    }
}
