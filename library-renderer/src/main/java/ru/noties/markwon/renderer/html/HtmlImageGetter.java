package ru.noties.markwon.renderer.html;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;

import ru.noties.markwon.UrlProcessor;
import ru.noties.markwon.spans.AsyncDrawable;

class HtmlImageGetter implements Html.ImageGetter {

    private final AsyncDrawable.Loader loader;
    private final UrlProcessor urlProcessor;

    HtmlImageGetter(@NonNull AsyncDrawable.Loader loader, @Nullable UrlProcessor urlProcessor) {
        this.loader = loader;
        this.urlProcessor = urlProcessor;
    }

    @Override
    public Drawable getDrawable(String source) {
        final String destination;
        if (urlProcessor == null) {
            destination = source;
        } else {
            destination = urlProcessor.process(source);
        }
        return new AsyncDrawable(destination, loader);
    }
}
