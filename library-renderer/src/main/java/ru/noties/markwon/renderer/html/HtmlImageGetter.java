package ru.noties.markwon.renderer.html;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Html;

import ru.noties.markwon.spans.AsyncDrawable;

class HtmlImageGetter implements Html.ImageGetter {

    private final AsyncDrawable.Loader loader;

    HtmlImageGetter(@NonNull AsyncDrawable.Loader loader) {
        this.loader = loader;
    }

    @Override
    public Drawable getDrawable(String source) {
        return new AsyncDrawable(source, loader);
    }
}
