package ru.noties.markwon.renderer.html2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.html.api.MarkwonHtmlParser;
import ru.noties.markwon.renderer.html2.tag.BlockquoteHandler;
import ru.noties.markwon.renderer.html2.tag.EmphasisHandler;
import ru.noties.markwon.renderer.html2.tag.HeadingHandler;
import ru.noties.markwon.renderer.html2.tag.ImageHandler;
import ru.noties.markwon.renderer.html2.tag.LinkHandler;
import ru.noties.markwon.renderer.html2.tag.ListHandler;
import ru.noties.markwon.renderer.html2.tag.StrikeHandler;
import ru.noties.markwon.renderer.html2.tag.StrongEmphasisHandler;
import ru.noties.markwon.renderer.html2.tag.SubScriptHandler;
import ru.noties.markwon.renderer.html2.tag.SuperScriptHandler;
import ru.noties.markwon.renderer.html2.tag.TagHandler;
import ru.noties.markwon.renderer.html2.tag.UnderlineHandler;

/**
 * @since 2.0.0
 */
public abstract class MarkwonHtmlRenderer {

    public abstract void render(
            @NonNull MarkwonConfiguration configuration,
            @NonNull SpannableBuilder builder,
            @NonNull MarkwonHtmlParser parser
    );

    @Nullable
    public abstract TagHandler tagHandler(@NonNull String tagName);

    @NonNull
    public static MarkwonHtmlRenderer create() {
        return builderWithDefaults().build();
    }

    @NonNull
    public static Builder builderWithDefaults() {

        final EmphasisHandler emphasisHandler = new EmphasisHandler();
        final StrongEmphasisHandler strongEmphasisHandler = new StrongEmphasisHandler();
        final StrikeHandler strikeHandler = new StrikeHandler();
        final UnderlineHandler underlineHandler = new UnderlineHandler();
        final ListHandler listHandler = new ListHandler();

        return builder()
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

    public static class Builder {

        private final Map<String, TagHandler> tagHandlers = new HashMap<>(2);

        public Builder handler(@NonNull String tagName, @NonNull TagHandler tagHandler) {
            tagHandlers.put(tagName.toLowerCase(Locale.US), tagHandler);
            return this;
        }

        @NonNull
        public MarkwonHtmlRenderer build() {
            return new MarkwonHtmlRendererImpl(Collections.unmodifiableMap(tagHandlers));
        }
    }
}
