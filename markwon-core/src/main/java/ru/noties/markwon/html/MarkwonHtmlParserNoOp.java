package ru.noties.markwon.html;

import androidx.annotation.NonNull;

import java.util.Collections;

class MarkwonHtmlParserNoOp extends MarkwonHtmlParser {
    @Override
    public <T extends Appendable & CharSequence> void processFragment(@NonNull T output, @NonNull String htmlFragment) {
        // no op
    }

    @Override
    public void flushInlineTags(int documentLength, @NonNull FlushAction<HtmlTag.Inline> action) {
        action.apply(Collections.<HtmlTag.Inline>emptyList());
    }

    @Override
    public void flushBlockTags(int documentLength, @NonNull FlushAction<HtmlTag.Block> action) {
        action.apply(Collections.<HtmlTag.Block>emptyList());
    }

    @Override
    public void reset() {
        // no op
    }
}
