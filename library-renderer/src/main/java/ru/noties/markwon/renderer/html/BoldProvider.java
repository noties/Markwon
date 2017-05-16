package ru.noties.markwon.renderer.html;

import ru.noties.markwon.spans.StrongEmphasisSpan;

class BoldProvider implements SpannableHtmlParser.SpanProvider {
    @Override
    public Object provide() {
        return new StrongEmphasisSpan();
    }
}
