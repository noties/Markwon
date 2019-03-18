package ru.noties.markwon.image.gif;

import android.support.annotation.NonNull;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.image.ImagesPlugin;
import ru.noties.markwon.priority.Priority;

public class GifPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static GifPlugin create() {
        return create(true);
    }

    @NonNull
    public static GifPlugin create(boolean autoPlay) {
        return new GifPlugin(autoPlay);
    }

    private final boolean autoPlay;

    public GifPlugin(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    @Override
    public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {
        builder.addMediaDecoder(GifMediaDecoder.CONTENT_TYPE, GifMediaDecoder.create(autoPlay));
    }

    @NonNull
    @Override
    public Priority priority() {
        return Priority.after(ImagesPlugin.class);
    }
}
