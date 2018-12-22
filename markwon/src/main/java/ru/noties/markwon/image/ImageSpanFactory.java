package ru.noties.markwon.image;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;

public class ImageSpanFactory implements SpanFactory {
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps context) {
        return new AsyncDrawableSpan(
                configuration.theme(),
                new AsyncDrawable(
                        ImageProps.DESTINATION.require(context),
                        configuration.asyncDrawableLoader(),
                        configuration.imageSizeResolver(),
                        ImageProps.IMAGE_SIZE.get(context)
                ),
                AsyncDrawableSpan.ALIGN_BOTTOM,
                ImageProps.REPLACEMENT_TEXT_IS_LINK.get(context, false)
        );
    }
}
