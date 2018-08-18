package ru.noties.markwon.html.api;

import android.support.annotation.NonNull;

import java.util.List;

public abstract class MarkwonHtmlParser {

    @NonNull
    public static MarkwonHtmlParser noOp() {
        return new MarkwonHtmlParserNoOp();
    }

    public interface FlushAction<T> {
        void apply(@NonNull List<T> tags);
    }

    public abstract <T extends Appendable & CharSequence> void processFragment(
            @NonNull T output,
            @NonNull String htmlFragment);

    // clear all pending tags (if any)
    // todo: we also can do this: if supplied value is -1 (for example) we ignore tags that are not closed
    public abstract void flushInlineTags(
            int documentLength,
            @NonNull FlushAction<HtmlTag.Inline> action);

    // clear all pending blocks if any
    // todo: we also can do this: if supplied value is -1 (for example) we ignore tags that are not closed
    public abstract void flushBlockTags(
            int documentLength,
            @NonNull FlushAction<HtmlTag.Block> action);

    public abstract void reset();

}
