package ru.noties.markwon.spans;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class SubScriptSpan extends MetricAffectingSpan {

    private final SpannableTheme mTheme;

    public SubScriptSpan(@NonNull SpannableTheme theme) {
        mTheme = theme;
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
        mTheme.applySubScriptStyle(paint);
    }
}
