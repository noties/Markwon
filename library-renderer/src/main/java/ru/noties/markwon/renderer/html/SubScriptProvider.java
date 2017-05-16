package ru.noties.markwon.renderer.html;

import ru.noties.markwon.spans.SpannableTheme;
import ru.noties.markwon.spans.SubScriptSpan;

class SubScriptProvider implements SpannableHtmlParser.SpanProvider {

    private final SpannableTheme theme;

    public SubScriptProvider(SpannableTheme theme) {
        this.theme = theme;
    }

    @Override
    public Object provide() {
        return new SubScriptSpan(theme);
    }
}
