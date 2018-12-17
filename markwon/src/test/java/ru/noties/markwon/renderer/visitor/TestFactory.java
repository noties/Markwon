package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.core.MarkwonSpannableFactory;
import ru.noties.markwon.core.spans.LinkSpan;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.image.ImageSize;
import ru.noties.markwon.image.ImageSizeResolver;

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
import static ru.noties.markwon.renderer.visitor.TestSpan.STRONG_EMPHASIS;
import static ru.noties.markwon.renderer.visitor.TestSpan.THEMATIC_BREAK;

class TestFactory implements MarkwonSpannableFactory {

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
    public Object blockQuote(@NonNull MarkwonTheme theme) {
        return new TestSpan(BLOCK_QUOTE);
    }

    @Nullable
    @Override
    public Object code(@NonNull MarkwonTheme theme, boolean multiline) {
        final String name = multiline
                ? CODE_BLOCK
                : CODE;
        return new TestSpan(name);
    }

    @Nullable
    @Override
    public Object orderedListItem(@NonNull MarkwonTheme theme, int startNumber) {
        return new TestSpan(ORDERED_LIST, map("start", startNumber));
    }

    @Nullable
    @Override
    public Object bulletListItem(@NonNull MarkwonTheme theme, int level) {
        return new TestSpan(BULLET_LIST, map("level", level));
    }

    @Nullable
    @Override
    public Object thematicBreak(@NonNull MarkwonTheme theme) {
        return new TestSpan(THEMATIC_BREAK);
    }

    @Nullable
    @Override
    public Object heading(@NonNull MarkwonTheme theme, int level) {
        return new TestSpan(HEADING + level);
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
    public Object image(@NonNull MarkwonTheme theme, @NonNull String destination, @NonNull AsyncDrawableLoader loader, @NonNull ImageSizeResolver imageSizeResolver, @Nullable ImageSize imageSize, boolean replacementTextIsLink) {
        return new TestSpan(IMAGE, map(
                Pair.of("src", destination),
                Pair.of("imageSize", imageSize),
                Pair.of("replacementTextIsLink", replacementTextIsLink)
        ));
    }

    @Nullable
    @Override
    public Object link(@NonNull MarkwonTheme theme, @NonNull String destination, @NonNull LinkSpan.Resolver resolver) {
        return new TestSpan(LINK, map("href", destination));
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
            map.put(pair.key, pair.value == null ? null : String.valueOf(pair.value));
        }
        return map;
    }
}
