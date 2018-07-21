package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;

import ru.noties.markwon.SpannableFactory;
import ru.noties.markwon.spans.SpannableTheme;

class SuperScriptProvider implements SpannableHtmlParser.SpanProvider {

    private final SpannableFactory factory;
    private final SpannableTheme theme;

    SuperScriptProvider(@NonNull SpannableFactory factory, @NonNull SpannableTheme theme) {
        this.factory = factory;
        this.theme = theme;
    }

    @Override
    public Object provide(@NonNull SpannableHtmlParser.Tag tag) {
        return factory.superScript(theme);
    }
}
