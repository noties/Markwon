package ru.noties.markwon.gif;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import org.commonmark.node.Image;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonSpansFactory;
import ru.noties.markwon.R;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.image.AsyncDrawableSpan;
import ru.noties.markwon.image.ImageProps;

public class GifAwarePlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static GifAwarePlugin create(@NonNull Context context) {
        return new GifAwarePlugin(context);
    }

    private final Context context;
    private final GifProcessor processor;

    GifAwarePlugin(@NonNull Context context) {
        this.context = context;
        this.processor = GifProcessor.create();
    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {

        final GifPlaceholder gifPlaceholder = new GifPlaceholder(
                context.getResources().getDrawable(R.drawable.ic_play_circle_filled_18dp_white),
                0x20000000
        );

        builder.setFactory(Image.class, new SpanFactory() {
            @Override
            public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps context) {
                return new AsyncDrawableSpan(
                        configuration.theme(),
                        new GifAwareAsyncDrawable(
                                gifPlaceholder,
                                ImageProps.DESTINATION.require(context),
                                configuration.asyncDrawableLoader(),
                                configuration.imageSizeResolver(),
                                ImageProps.IMAGE_SIZE.get(context)
                        ),
                        AsyncDrawableSpan.ALIGN_BOTTOM,
                        ImageProps.REPLACEMENT_TEXT_IS_LINK.get(context, false)
                );
            }
        });
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        processor.process(textView);
    }
}
