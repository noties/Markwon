package ru.noties.markwon.image.svg;

import android.content.res.Resources;
import androidx.annotation.NonNull;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.image.ImagesPlugin;
import ru.noties.markwon.priority.Priority;

public class SvgPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static SvgPlugin create(@NonNull Resources resources) {
        return new SvgPlugin(resources);
    }

    private final Resources resources;

    public SvgPlugin(@NonNull Resources resources) {
        this.resources = resources;
    }

    @Override
    public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {
        builder.addMediaDecoder(SvgMediaDecoder.CONTENT_TYPE, SvgMediaDecoder.create(resources));
    }

    @NonNull
    @Override
    public Priority priority() {
        return Priority.after(ImagesPlugin.class);
    }
}
