package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;

import java.util.List;

import ru.noties.markwon.renderer.ImageSize;
import ru.noties.markwon.renderer.ImageSizeResolver;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.AsyncDrawableSpan;
import ru.noties.markwon.spans.BlockQuoteSpan;
import ru.noties.markwon.spans.BulletListItemSpan;
import ru.noties.markwon.spans.CodeSpan;
import ru.noties.markwon.spans.EmphasisSpan;
import ru.noties.markwon.spans.HeadingSpan;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.OrderedListItemSpan;
import ru.noties.markwon.spans.SpannableTheme;
import ru.noties.markwon.spans.StrongEmphasisSpan;
import ru.noties.markwon.spans.SubScriptSpan;
import ru.noties.markwon.spans.SuperScriptSpan;
import ru.noties.markwon.spans.TableRowSpan;
import ru.noties.markwon.spans.TaskListSpan;
import ru.noties.markwon.spans.ThematicBreakSpan;

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
    public Object blockQuote(@NonNull SpannableTheme theme) {
        return new BlockQuoteSpan(theme);
    }

    @Nullable
    @Override
    public Object code(@NonNull SpannableTheme theme, boolean multiline) {
        return new CodeSpan(theme, multiline);
    }

    @Nullable
    @Override
    public Object orderedListItem(@NonNull SpannableTheme theme, int startNumber) {
        // todo| in order to provide real RTL experience there must be a way to provide this string
        return new OrderedListItemSpan(theme, String.valueOf(startNumber) + "." + '\u00a0');
    }

    @Nullable
    @Override
    public Object bulletListItem(@NonNull SpannableTheme theme, int level) {
        return new BulletListItemSpan(theme, level);
    }

    @Nullable
    @Override
    public Object thematicBreak(@NonNull SpannableTheme theme) {
        return new ThematicBreakSpan(theme);
    }

    @Nullable
    @Override
    public Object heading(@NonNull SpannableTheme theme, int level) {
        return new HeadingSpan(theme, level);
    }

    @Nullable
    @Override
    public Object strikethrough() {
        return new StrikethroughSpan();
    }

    @Nullable
    @Override
    public Object taskListItem(@NonNull SpannableTheme theme, int blockIndent, boolean isDone) {
        return new TaskListSpan(theme, blockIndent, isDone);
    }

    @Nullable
    @Override
    public Object tableRow(@NonNull SpannableTheme theme, @NonNull List<TableRowSpan.Cell> cells, boolean isHeader, boolean isOdd) {
        return new TableRowSpan(theme, cells, isHeader, isOdd);
    }

    @Nullable
    @Override
    public Object image(@NonNull SpannableTheme theme, @NonNull String destination, @NonNull AsyncDrawable.Loader loader, @NonNull ImageSizeResolver imageSizeResolver, @Nullable ImageSize imageSize, boolean replacementTextIsLink) {
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
    public Object link(@NonNull SpannableTheme theme, @NonNull String destination, @NonNull LinkSpan.Resolver resolver) {
        return new LinkSpan(theme, destination, resolver);
    }

    @Nullable
    @Override
    public Object superScript(@NonNull SpannableTheme theme) {
        return new SuperScriptSpan(theme);
    }

    @Override
    public Object subScript(@NonNull SpannableTheme theme) {
        return new SubScriptSpan(theme);
    }

    @Nullable
    @Override
    public Object underline() {
        return new UnderlineSpan();
    }
}
