package ru.noties.markwon.renderer.html;

import ru.noties.markwon.spans.SpannableTheme;
import ru.noties.markwon.spans.SuperScriptSpan;

class SuperScriptProvider implements SpannableHtmlParser.SpanProvider {

    private final SpannableTheme theme;

    SuperScriptProvider(SpannableTheme theme) {
        this.theme = theme;
    }

    @Override
    public Object provide() {
        return new SuperScriptSpan(theme);
    }
}
