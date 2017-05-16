package ru.noties.markwon.renderer.html;

import android.text.style.StrikethroughSpan;

class StrikeProvider implements SpannableHtmlParser.SpanProvider {
    @Override
    public Object provide() {
        return new StrikethroughSpan();
    }
}
