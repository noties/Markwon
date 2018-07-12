package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.text.style.StrikethroughSpan;

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
import ru.noties.markwon.spans.SpanFactory;
import ru.noties.markwon.spans.SpannableTheme;
import ru.noties.markwon.spans.StrongEmphasisSpan;
import ru.noties.markwon.spans.TaskListSpan;
import ru.noties.markwon.spans.ThematicBreakSpan;

public class SpanFactoryDef implements SpanFactory {
    @NonNull
    private final SpannableTheme theme;

    @NonNull
    private final LinkSpan.Resolver linkResolver;

    @NonNull
    private final AsyncDrawable.Loader drawableLoader;

    @NonNull
    private final ImageSizeResolver imageSizeResolver;

    public SpanFactoryDef(@NonNull SpannableTheme theme,
                          @NonNull LinkSpan.Resolver linkResolver,
                          @NonNull AsyncDrawable.Loader drawableLoader,
                          @NonNull ImageSizeResolver imageSizeResolver) {
        this.theme = theme;
        this.linkResolver = linkResolver;
        this.drawableLoader = drawableLoader;
        this.imageSizeResolver = imageSizeResolver;
    }

    @NonNull
    @Override
    public Object createBlockQuote() {
        return new BlockQuoteSpan(theme);
    }

    @NonNull
    @Override
    public Object createBulletListItem(int level) {
        return new BulletListItemSpan(theme, level);
    }

    @NonNull
    @Override
    public Object createCode(boolean multiline) {
        return new CodeSpan(theme, multiline);
    }

    @NonNull
    @Override
    public Object createEmphasis() {
        return new EmphasisSpan();
    }

    @NonNull
    @Override
    public Object createHeading(int level) {
        return new HeadingSpan(theme, level);
    }

    @NonNull
    @Override
    public Object createImage(@NonNull String destination, boolean link) {
        return new AsyncDrawableSpan(
                theme,
                new AsyncDrawable(
                        destination,
                        drawableLoader,
                        imageSizeResolver,
                        null
                ),
                AsyncDrawableSpan.ALIGN_BOTTOM,
                link
        );
    }

    @NonNull
    @Override
    public Object createLink(@NonNull String destination) {
        return new LinkSpan(theme, destination, linkResolver);
    }

    @NonNull
    @Override
    public Object createOrderedListItem(int order) {
        // todo| in order to provide real RTL experience there must be a way to provide this string
        return new OrderedListItemSpan(theme, String.valueOf(order) + "." + '\u00a0');
    }

    @NonNull
    @Override
    public Object createStrikethrough() {
        return new StrikethroughSpan();
    }

    @NonNull
    @Override
    public Object createStrongEmphasis() {
        return new StrongEmphasisSpan();
    }

    @NonNull
    @Override
    public Object createTaskList(int indent, boolean done) {
        return new TaskListSpan(theme, indent, done);
    }

    @NonNull
    @Override
    public Object createThematicBreak() {
        return new ThematicBreakSpan(theme);
    }
}
