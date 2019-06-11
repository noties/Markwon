package io.noties.markwon.app.gif;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.commonmark.node.Image;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.app.R;
import io.noties.markwon.image.AsyncDrawableSpan;
import io.noties.markwon.image.ImageProps;

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
            public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                return new AsyncDrawableSpan(
                        configuration.theme(),
                        new GifAwareAsyncDrawable(
                                gifPlaceholder,
                                ImageProps.DESTINATION.require(props),
                                configuration.asyncDrawableLoader(),
                                configuration.imageSizeResolver(),
                                ImageProps.IMAGE_SIZE.get(props)
                        ),
                        AsyncDrawableSpan.ALIGN_BOTTOM,
                        ImageProps.REPLACEMENT_TEXT_IS_LINK.get(props, false)
                );
            }
        });
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        processor.process(textView);
    }
}
