package ru.noties.markwon.renderer.html;

import ru.noties.markwon.spans.EmphasisSpan;

class ItalicsProvider implements SpannableHtmlParser.SpanProvider {
    @Override
    public Object provide() {
        return new EmphasisSpan();
    }
}
