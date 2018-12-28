package ru.noties.markwon.sample;

import android.support.annotation.StringRes;

public enum SampleItem {

    // all usages of markwon without plugins (parse, render, setMarkwon, etc)
    CORE(R.string.sample_core),

    LATEX(R.string.sample_latex),

    CUSTOM_EXTENSION(R.string.sample_custom_extension),

    ;

    private final int textResId;

    SampleItem(@StringRes int textResId) {
        this.textResId = textResId;
    }

    @StringRes
    public int textResId() {
        return textResId;
    }
}
