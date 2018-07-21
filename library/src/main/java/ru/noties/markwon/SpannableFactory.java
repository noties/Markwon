package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import ru.noties.markwon.renderer.ImageSize;
import ru.noties.markwon.renderer.ImageSizeResolver;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.SpannableTheme;
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
    Object blockQuote(@NonNull SpannableTheme theme);

    @Nullable
    Object code(@NonNull SpannableTheme theme, boolean multiline);

    @Nullable
    Object orderedListItem(@NonNull SpannableTheme theme, int startNumber);

    @Nullable
    Object bulletListItem(@NonNull SpannableTheme theme, int level);

    @Nullable
    Object thematicBreak(@NonNull SpannableTheme theme);

    @Nullable
    Object heading(@NonNull SpannableTheme theme, int level);

    @Nullable
    Object strikethrough();

    @Nullable
    Object taskListItem(@NonNull SpannableTheme theme, int blockIndent, boolean isDone);

    @Nullable
    Object tableRow(
            @NonNull SpannableTheme theme,
            @NonNull List<TableRowSpan.Cell> cells,
            boolean isHeader,
            boolean isOdd);

    @Nullable
    Object image(
            @NonNull SpannableTheme theme,
            @NonNull String destination,
            @NonNull AsyncDrawable.Loader loader,
            @NonNull ImageSizeResolver imageSizeResolver,
            @Nullable ImageSize imageSize,
            boolean replacementTextIsLink);

    @Nullable
    Object link(
            @NonNull SpannableTheme theme,
            @NonNull String destination,
            @NonNull LinkSpan.Resolver resolver);

    // Currently used by HTML parser
    @Nullable
    Object superScript(@NonNull SpannableTheme theme);

    // Currently used by HTML parser
    @Nullable
    Object subScript(@NonNull SpannableTheme theme);

    // Currently used by HTML parser
    @Nullable
    Object underline();
}
