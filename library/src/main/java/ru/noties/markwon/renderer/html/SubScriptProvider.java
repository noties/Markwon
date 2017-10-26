package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;

import ru.noties.markwon.spans.SpannableTheme;
import ru.noties.markwon.spans.SubScriptSpan;

class SubScriptProvider implements SpannableHtmlParser.SpanProvider {

    private final SpannableTheme mTheme;

    public SubScriptProvider(SpannableTheme theme) {
        mTheme = theme;
    }

    @Override
    public Object provide(@NonNull SpannableHtmlParser.Tag tag) {
        return new SubScriptSpan(mTheme);
    }
}
