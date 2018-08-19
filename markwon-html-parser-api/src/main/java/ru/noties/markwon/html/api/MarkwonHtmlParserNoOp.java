package ru.noties.markwon.html.api;

import android.support.annotation.NonNull;

/**
 * @see MarkwonHtmlParser
 * @since 2.0.0
 */
class MarkwonHtmlParserNoOp extends MarkwonHtmlParser {

    @Override
    public <T extends Appendable & CharSequence> void processFragment(@NonNull T output, @NonNull String htmlFragment) {

    }

    @Override
    public void flushInlineTags(int documentLength, @NonNull FlushAction<HtmlTag.Inline> action) {

    }

    @Override
    public void flushBlockTags(int documentLength, @NonNull FlushAction<HtmlTag.Block> action) {

    }

    @Override
    public void reset() {

    }
}
