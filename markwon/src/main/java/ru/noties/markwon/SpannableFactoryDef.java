package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;

import ru.noties.markwon.image.AsyncDrawable;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.renderer.ImageSize;
import ru.noties.markwon.renderer.ImageSizeResolver;
import ru.noties.markwon.spans.AsyncDrawableSpan;
import ru.noties.markwon.spans.BlockQuoteSpan;
import ru.noties.markwon.spans.BulletListItemSpan;
import ru.noties.markwon.spans.CodeSpan;
import ru.noties.markwon.spans.EmphasisSpan;
import ru.noties.markwon.spans.HeadingSpan;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.MarkwonTheme;
import ru.noties.markwon.spans.OrderedListItemSpan;
import ru.noties.markwon.spans.StrongEmphasisSpan;
import ru.noties.markwon.spans.SubScriptSpan;
import ru.noties.markwon.spans.SuperScriptSpan;
import ru.noties.markwon.spans.ThematicBreakSpan;

/**
 * @since 1.1.0
 */
public class SpannableFactoryDef implements SpannableFactory {

    @NonNull
    public static SpannableFactoryDef create() {
        return new SpannableFactoryDef();
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

    @Nullable
    @Override
    public Object strikethrough() {
        return new StrikethroughSpan();
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
    public Object image(@NonNull MarkwonTheme theme, @NonNull String destination, @NonNull AsyncDrawableLoader loader, @NonNull ImageSizeResolver imageSizeResolver, @Nullable ImageSize imageSize, boolean replacementTextIsLink) {
        return new AsyncDrawableSpan(
                theme,
                new AsyncDrawable(
                        destination,
                        loader,
                        imageSizeResolver,
                        imageSize
                ),
                AsyncDrawableSpan.ALIGN_BOTTOM,
                replacementTextIsLink
        );
    }

    @Nullable
    @Override
    public Object link(@NonNull MarkwonTheme theme, @NonNull String destination, @NonNull LinkSpan.Resolver resolver) {
        return new LinkSpan(theme, destination, resolver);
    }
}
