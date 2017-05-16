package ru.noties.markwon.renderer.html;

import android.text.style.UnderlineSpan;

class UnderlineProvider implements SpannableHtmlParser.SpanProvider {

    @Override
    public Object provide() {
        return new UnderlineSpan();
    }
}
