package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.noties.markwon.SpannableFactory;
import ru.noties.markwon.renderer.ImageSize;
import ru.noties.markwon.renderer.ImageSizeResolver;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.SpannableTheme;
import ru.noties.markwon.spans.TableRowSpan;

import static ru.noties.markwon.renderer.visitor.TestSpan.BLOCK_QUOTE;
import static ru.noties.markwon.renderer.visitor.TestSpan.BULLET_LIST;
import static ru.noties.markwon.renderer.visitor.TestSpan.CODE;
import static ru.noties.markwon.renderer.visitor.TestSpan.CODE_BLOCK;
import static ru.noties.markwon.renderer.visitor.TestSpan.EMPHASIS;
import static ru.noties.markwon.renderer.visitor.TestSpan.HEADING;
import static ru.noties.markwon.renderer.visitor.TestSpan.IMAGE;
import static ru.noties.markwon.renderer.visitor.TestSpan.LINK;
import static ru.noties.markwon.renderer.visitor.TestSpan.ORDERED_LIST;
import static ru.noties.markwon.renderer.visitor.TestSpan.PARAGRAPH;
import static ru.noties.markwon.renderer.visitor.TestSpan.STRIKE_THROUGH;
import static ru.noties.markwon.renderer.visitor.TestSpan.STRONG_EMPHASIS;
import static ru.noties.markwon.renderer.visitor.TestSpan.SUB_SCRIPT;
import static ru.noties.markwon.renderer.visitor.TestSpan.SUPER_SCRIPT;
import static ru.noties.markwon.renderer.visitor.TestSpan.TABLE_ROW;
import static ru.noties.markwon.renderer.visitor.TestSpan.TASK_LIST;
import static ru.noties.markwon.renderer.visitor.TestSpan.THEMATIC_BREAK;
import static ru.noties.markwon.renderer.visitor.TestSpan.UNDERLINE;

class TestFactory implements SpannableFactory {

    private final boolean useParagraphs;

    TestFactory(boolean useParagraphs) {
        this.useParagraphs = useParagraphs;
    }

    @Nullable
    @Override
    public Object strongEmphasis() {
        return new TestSpan(STRONG_EMPHASIS);
    }

    @Nullable
    @Override
    public Object emphasis() {
        return new TestSpan(EMPHASIS);
    }

    @Nullable
    @Override
    public Object blockQuote(@NonNull SpannableTheme theme) {
        return new TestSpan(BLOCK_QUOTE);
    }

    @Nullable
    @Override
    public Object code(@NonNull SpannableTheme theme, boolean multiline) {
        final String name = multiline
                ? CODE_BLOCK
                : CODE;
        return new TestSpan(name);
    }

    @Nullable
    @Override
    public Object orderedListItem(@NonNull SpannableTheme theme, int startNumber) {
        return new TestSpan(ORDERED_LIST, map("start", startNumber));
    }

    @Nullable
    @Override
    public Object bulletListItem(@NonNull SpannableTheme theme, int level) {
        return new TestSpan(BULLET_LIST, map("level", level));
    }

    @Nullable
    @Override
    public Object thematicBreak(@NonNull SpannableTheme theme) {
        return new TestSpan(THEMATIC_BREAK);
    }

    @Nullable
    @Override
    public Object heading(@NonNull SpannableTheme theme, int level) {
        return new TestSpan(HEADING + level);
    }

    @Nullable
    @Override
    public Object strikethrough() {
        return new TestSpan(STRIKE_THROUGH);
    }

    @Nullable
    @Override
    public Object taskListItem(@NonNull SpannableTheme theme, int blockIndent, boolean isDone) {
        return new TestSpan(TASK_LIST, map(
                Pair.of("blockIdent", blockIndent),
                Pair.of("done", isDone)
        ));
    }

    @Nullable
    @Override
    public Object tableRow(@NonNull SpannableTheme theme, @NonNull List<TableRowSpan.Cell> cells, boolean isHeader, boolean isOdd) {
        return new TestSpan(TABLE_ROW, map(
                Pair.of("cells", cells),
                Pair.of("header", isHeader),
                Pair.of("odd", isOdd)
        ));
    }

    @Nullable
    @Override
    public Object paragraph(boolean inTightList) {
        return !useParagraphs
                ? null
                : new TestSpan(PARAGRAPH);
    }

    @Nullable
    @Override
    public Object image(@NonNull SpannableTheme theme, @NonNull String destination, @NonNull AsyncDrawable.Loader loader, @NonNull ImageSizeResolver imageSizeResolver, @Nullable ImageSize imageSize, boolean replacementTextIsLink) {
        return new TestSpan(IMAGE, map(
                Pair.of("src", destination),
                Pair.of("imageSize", imageSize),
                Pair.of("replacementTextIsLink", replacementTextIsLink)
        ));
    }

    @Nullable
    @Override
    public Object link(@NonNull SpannableTheme theme, @NonNull String destination, @NonNull LinkSpan.Resolver resolver) {
        return new TestSpan(LINK, map("href", destination));
    }

    @Nullable
    @Override
    public Object superScript(@NonNull SpannableTheme theme) {
        return new TestSpan(SUPER_SCRIPT);
    }

    @Nullable
    @Override
    public Object subScript(@NonNull SpannableTheme theme) {
        return new TestSpan(SUB_SCRIPT);
    }

    @Nullable
    @Override
    public Object underline() {
        return new TestSpan(UNDERLINE);
    }

    @NonNull
    private static Map<String, String> map(@NonNull String key, @Nullable Object value) {
        return Collections.singletonMap(key, String.valueOf(value));
    }

    private static class Pair {

        static Pair of(@NonNull String key, @Nullable Object value) {
            return new Pair(key, value);
        }

        final String key;
        final Object value;

        Pair(@NonNull String key, @Nullable Object value) {
            this.key = key;
            this.value = value;
        }
    }

    @NonNull
    private static Map<String, String> map(Pair... pairs) {
        final int length = pairs.length;
        final Map<String, String> map = new HashMap<>(length);
        for (Pair pair : pairs) {
            map.put(pair.key, String.valueOf(pair.value));
        }
        return map;
    }
}
