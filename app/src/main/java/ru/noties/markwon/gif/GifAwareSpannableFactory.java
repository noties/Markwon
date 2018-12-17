package ru.noties.markwon.gif;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.core.MarkwonSpannableFactoryDef;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.image.ImageSize;
import ru.noties.markwon.image.ImageSizeResolver;
import ru.noties.markwon.core.spans.AsyncDrawableSpan;

public class GifAwareSpannableFactory extends MarkwonSpannableFactoryDef {

    private final GifPlaceholder gifPlaceholder;

    public GifAwareSpannableFactory(@NonNull GifPlaceholder gifPlaceholder) {
        this.gifPlaceholder = gifPlaceholder;
    }

    @Nullable
    @Override
    public Object image(@NonNull MarkwonTheme theme, @NonNull String destination, @NonNull AsyncDrawableLoader loader, @NonNull ImageSizeResolver imageSizeResolver, @Nullable ImageSize imageSize, boolean replacementTextIsLink) {
        return new AsyncDrawableSpan(
                theme,
                new GifAwareAsyncDrawable(
                        gifPlaceholder,
                        destination,
                        loader,
                        imageSizeResolver,
                        imageSize
                ),
                AsyncDrawableSpan.ALIGN_BOTTOM,
                replacementTextIsLink
        );
    }
}
