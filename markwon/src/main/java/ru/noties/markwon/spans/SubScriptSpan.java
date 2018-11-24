package ru.noties.markwon.spans;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class SubScriptSpan extends MetricAffectingSpan {

    private final MarkwonTheme theme;

    public SubScriptSpan(@NonNull MarkwonTheme theme) {
        this.theme = theme;
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        apply(tp);
    }

    @Override
    public void updateMeasureState(TextPaint tp) {
        apply(tp);
    }

    private void apply(TextPaint paint) {
        theme.applySubScriptStyle(paint);
    }
}
