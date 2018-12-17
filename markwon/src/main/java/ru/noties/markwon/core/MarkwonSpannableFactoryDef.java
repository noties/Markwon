package ru.noties.markwon.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.core.spans.BlockQuoteSpan;
import ru.noties.markwon.core.spans.BulletListItemSpan;
import ru.noties.markwon.core.spans.CodeSpan;
import ru.noties.markwon.core.spans.EmphasisSpan;
import ru.noties.markwon.core.spans.HeadingSpan;
import ru.noties.markwon.core.spans.LinkSpan;
import ru.noties.markwon.core.spans.OrderedListItemSpan;
import ru.noties.markwon.core.spans.StrongEmphasisSpan;
import ru.noties.markwon.core.spans.ThematicBreakSpan;

/**
 * @since 1.1.0
 */
public class MarkwonSpannableFactoryDef implements MarkwonSpannableFactory {

    @NonNull
    public static MarkwonSpannableFactoryDef create() {
        return new MarkwonSpannableFactoryDef();
    }

    @Nullable
    @Override
    public Object strongEmphasis() {
        return new StrongEmphasisSpan();
    }

    @Nullable
    @Override
    public Object emphasis() {
        return new EmphasisSpan();
    }

    @Nullable
    @Override
    public Object blockQuote(@NonNull MarkwonTheme theme) {
        return new BlockQuoteSpan(theme);
    }

    @Nullable
    @Override
    public Object code(@NonNull MarkwonTheme theme, boolean multiline) {
        return new CodeSpan(theme, multiline);
    }

    @Nullable
    @Override
    public Object orderedListItem(@NonNull MarkwonTheme theme, int startNumber) {
        // todo| in order to provide real RTL experience there must be a way to provide this string
        return new OrderedListItemSpan(theme, String.valueOf(startNumber) + "." + '\u00a0');
    }

    @Nullable
    @Override
    public Object bulletListItem(@NonNull MarkwonTheme theme, int level) {
        return new BulletListItemSpan(theme, level);
    }

    @Nullable
    @Override
    public Object thematicBreak(@NonNull MarkwonTheme theme) {
        return new ThematicBreakSpan(theme);
    }

    @Nullable
    @Override
    public Object heading(@NonNull MarkwonTheme theme, int level) {
        return new HeadingSpan(theme, level);
    }

    /**
     * @since 1.1.1
     */
    @Nullable
    @Override
    public Object paragraph(boolean inTightList) {
        return null;
    }

    @Nullable
    @Override
    public Object link(@NonNull MarkwonTheme theme, @NonNull String destination, @NonNull LinkSpan.Resolver resolver) {
        return new LinkSpan(theme, destination, resolver);
    }
}
