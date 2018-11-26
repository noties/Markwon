package ru.noties.markwon.html.impl.span;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import ru.noties.markwon.html.impl.MarkwonHtmlRendererImpl;

public class SubScriptSpan extends MetricAffectingSpan {

    @Override
    public void updateDrawState(TextPaint tp) {
        apply(tp);
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint tp) {
        apply(tp);
    }

    private void apply(TextPaint paint) {
        paint.setTextSize(paint.getTextSize() * MarkwonHtmlRendererImpl.SCRIPT_DEF_TEXT_SIZE_RATIO);
        paint.baselineShift -= (int) (paint.ascent() / 2);
    }
}
