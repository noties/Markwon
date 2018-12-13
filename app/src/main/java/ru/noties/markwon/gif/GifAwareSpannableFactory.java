package ru.noties.markwon.gif;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.SpannableFactoryDef;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.image.ImageSize;
import ru.noties.markwon.image.ImageSizeResolver;
import ru.noties.markwon.core.spans.AsyncDrawableSpan;
import ru.noties.markwon.core.MarkwonTheme;

public class GifAwareSpannableFactory extends SpannableFactoryDef {

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
