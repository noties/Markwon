package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;

import ru.noties.markwon.SpannableFactory;

class UnderlineProvider implements SpannableHtmlParser.SpanProvider {

    private final SpannableFactory factory;

    /**
     * @since 1.1.0
     */
    UnderlineProvider(@NonNull SpannableFactory factory) {
        this.factory = factory;
    }

    @Override
    public Object provide(@NonNull SpannableHtmlParser.Tag tag) {
        return factory.underline();
    }
}
