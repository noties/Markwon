package ru.noties.markwon.spans;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class SubSpan extends MetricAffectingSpan {

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTextSize(tp.getTextSize() * .75F);
        tp.baselineShift -= (int) (tp.ascent() / 2);
    }

    @Override
    public void updateMeasureState(TextPaint tp) {
        tp.setTextSize(tp.getTextSize() * .75F);
        tp.baselineShift -= (int) (tp.ascent() / 2);
    }
}
