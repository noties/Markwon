package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import ru.noties.markwon.renderer.ImageSize;
import ru.noties.markwon.renderer.ImageSizeResolver;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.MarkwonTheme;
import ru.noties.markwon.spans.TableRowSpan;

/**
 * Each method can return null or a Span object or an array of spans
 *
 * @since 1.1.0
 */
public interface SpannableFactory {

    @Nullable
    Object strongEmphasis();

    @Nullable
    Object emphasis();

    @Nullable
    Object blockQuote(@NonNull MarkwonTheme theme);

    @Nullable
    Object code(@NonNull MarkwonTheme theme, boolean multiline);

    @Nullable
    Object orderedListItem(@NonNull MarkwonTheme theme, int startNumber);

    @Nullable
    Object bulletListItem(@NonNull MarkwonTheme theme, int level);

    @Nullable
    Object thematicBreak(@NonNull MarkwonTheme theme);

    @Nullable
    Object heading(@NonNull MarkwonTheme theme, int level);

    @Nullable
    Object strikethrough();

    @Nullable
    Object taskListItem(@NonNull MarkwonTheme theme, int blockIndent, boolean isDone);

    @Nullable
    Object tableRow(
            @NonNull MarkwonTheme theme,
            @NonNull List<TableRowSpan.Cell> cells,
            boolean isHeader,
            boolean isOdd);

    /**
     * @since 1.1.1
     */
    @Nullable
    Object paragraph(boolean inTightList);

    @Nullable
    Object image(
            @NonNull MarkwonTheme theme,
            @NonNull String destination,
            @NonNull AsyncDrawable.Loader loader,
            @NonNull ImageSizeResolver imageSizeResolver,
            @Nullable ImageSize imageSize,
            boolean replacementTextIsLink);

    @Nullable
    Object link(
            @NonNull MarkwonTheme theme,
            @NonNull String destination,
            @NonNull LinkSpan.Resolver resolver);

    // Currently used by HTML parser
    @Nullable
    Object superScript(@NonNull MarkwonTheme theme);

    // Currently used by HTML parser
    @Nullable
    Object subScript(@NonNull MarkwonTheme theme);

    // Currently used by HTML parser
    @Nullable
    Object underline();
}
