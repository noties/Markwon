package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

public class GifAwarePlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static GifAwarePlugin create(@NonNull Context context) {
        return new GifAwarePlugin(context);
    }

    private final Context context;
    private final GifProcessor processor;

    public GifAwarePlugin(@NonNull Context context) {
        this.context = context;
        this.processor = GifProcessor.create();
    }

    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
        final GifPlaceholder gifPlaceholder = new GifPlaceholder(
                context.getResources().getDrawable(R.drawable.ic_play_circle_filled_18dp_white),
                0x20000000
        );
        builder.factory(new GifAwareSpannableFactory(gifPlaceholder));
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        processor.process(textView);
    }
}
