package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;

import ru.noties.markwon.SpannableFactory;

class BoldProvider implements SpannableHtmlParser.SpanProvider {

    private final SpannableFactory factory;

    /**
     * @since 1.1.0
     */
    BoldProvider(@NonNull SpannableFactory factory) {
        this.factory = factory;
    }

    @Override
    public Object provide(@NonNull SpannableHtmlParser.Tag tag) {
        return factory.strongEmphasis();
    }
}
