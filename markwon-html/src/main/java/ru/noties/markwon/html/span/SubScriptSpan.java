package ru.noties.markwon.html.span;

import androidx.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import ru.noties.markwon.html.HtmlPlugin;

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
        paint.setTextSize(paint.getTextSize() * HtmlPlugin.SCRIPT_DEF_TEXT_SIZE_RATIO);
        paint.baselineShift -= (int) (paint.ascent() / 2);
    }
}
