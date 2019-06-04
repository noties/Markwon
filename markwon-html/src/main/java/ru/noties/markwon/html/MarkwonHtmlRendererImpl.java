package ru.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.html.tag.BlockquoteHandler;
import ru.noties.markwon.html.tag.EmphasisHandler;
import ru.noties.markwon.html.tag.HeadingHandler;
import ru.noties.markwon.html.tag.ImageHandler;
import ru.noties.markwon.html.tag.LinkHandler;
import ru.noties.markwon.html.tag.ListHandler;
import ru.noties.markwon.html.tag.StrikeHandler;
import ru.noties.markwon.html.tag.StrongEmphasisHandler;
import ru.noties.markwon.html.tag.SubScriptHandler;
import ru.noties.markwon.html.tag.SuperScriptHandler;
import ru.noties.markwon.html.tag.UnderlineHandler;

class MarkwonHtmlRendererImpl extends MarkwonHtmlRenderer {

    private final boolean allowNonClosedTags;
    private final Map<String, TagHandler> tagHandlers;

    @SuppressWarnings("WeakerAccess")
    MarkwonHtmlRendererImpl(boolean allowNonClosedTags, @NonNull Map<String, TagHandler> tagHandlers) {
        this.allowNonClosedTags = allowNonClosedTags;
        this.tagHandlers = tagHandlers;
    }

    @Override
    public void render(
            @NonNull final MarkwonVisitor visitor,
            @NonNull MarkwonHtmlParser parser) {

        final int end;
        if (!allowNonClosedTags) {
            end = HtmlTag.NO_END;
        } else {
            end = visitor.length();
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
                        handler.handle(visitor, MarkwonHtmlRendererImpl.this, inline);
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
                        handler.handle(visitor, MarkwonHtmlRendererImpl.this, block);
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

    static class Builder {

        private final Map<String, TagHandler> tagHandlers = new HashMap<>(2);
        private boolean allowNonClosedTags;
        private boolean excludeDefaults;

        private boolean isBuilt;

        void allowNonClosedTags(boolean allowNonClosedTags) {
            checkState();
            this.allowNonClosedTags = allowNonClosedTags;
        }

        void addHandler(@NonNull TagHandler tagHandler) {
            checkState();
            for (String tag : tagHandler.supportedTags()) {
                tagHandlers.put(tag, tagHandler);
            }
        }

        @Nullable
        TagHandler getHandler(@NonNull String tagName) {
            checkState();
            return tagHandlers.get(tagName);
        }

        public void excludeDefaults(boolean excludeDefaults) {
            checkState();
            this.excludeDefaults = excludeDefaults;
        }

        @NonNull
        public MarkwonHtmlRenderer build() {

            checkState();

            isBuilt = true;

            if (!excludeDefaults) {
                // register default handlers, check if a handler is present already for specified tag
                registerDefaultHandlers();
            }

            // okay, let's validate that we have at least one tagHandler registered
            // if we have none -> return no-op implementation
            return tagHandlers.size() > 0
                    ? new MarkwonHtmlRendererImpl(allowNonClosedTags, Collections.unmodifiableMap(tagHandlers))
                    : new MarkwonHtmlRendererNoOp();
        }

        private void checkState() {
            if (isBuilt) {
                throw new IllegalStateException("Builder has been already built");
            }
        }

        private void registerDefaultHandlers() {
            add(ImageHandler.create());
            add(new LinkHandler());
            add(new BlockquoteHandler());
            add(new SubScriptHandler());
            add(new SuperScriptHandler());
            add(new StrongEmphasisHandler());
            add(new StrikeHandler());
            add(new UnderlineHandler());
            add(new ListHandler());
            add(new EmphasisHandler());
            add(new HeadingHandler());
        }

        private void add(@NonNull TagHandler tagHandler) {
            for (String tag : tagHandler.supportedTags()) {
                if (!tagHandlers.containsKey(tag)) {
                    tagHandlers.put(tag, tagHandler);
                }
            }
        }
    }
}
