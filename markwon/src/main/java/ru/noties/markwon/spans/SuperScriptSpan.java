package ru.noties.markwon.spans;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class SuperScriptSpan extends MetricAffectingSpan {

    private final MarkwonTheme theme;

    public SuperScriptSpan(@NonNull MarkwonTheme theme) {
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
        theme.applySuperScriptStyle(paint);
    }
}
