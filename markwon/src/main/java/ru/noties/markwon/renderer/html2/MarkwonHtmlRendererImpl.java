package ru.noties.markwon.renderer.html2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;

import java.util.List;
import java.util.Map;

import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.html.api.HtmlTag;
import ru.noties.markwon.html.api.MarkwonHtmlParser;
import ru.noties.markwon.renderer.html2.tag.TagHandler;

class MarkwonHtmlRendererImpl extends MarkwonHtmlRenderer {

    private final Map<String, TagHandler> tagHandlers;

    MarkwonHtmlRendererImpl(@NonNull Map<String, TagHandler> tagHandlers) {
        this.tagHandlers = tagHandlers;
    }

    @Override
    public void render(
            @NonNull final SpannableConfiguration configuration,
            @NonNull final Spannable spannable,
            @NonNull MarkwonHtmlParser parser) {

        final int end;
        if (!configuration.htmlAllowNonClosedTags()) {
            end = HtmlTag.NO_END;
        } else {
            end = spannable.length();
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
                        handler.handle(configuration, spannable, inline);
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
                        handler.handle(configuration, spannable, block);
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
}
