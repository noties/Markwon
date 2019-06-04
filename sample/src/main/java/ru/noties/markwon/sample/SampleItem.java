package ru.noties.markwon.sample;

import androidx.annotation.StringRes;

public enum SampleItem {

    // all usages of markwon without plugins (parse, render, setMarkwon, etc)
    CORE(R.string.sample_core),

    BASIC_PLUGINS(R.string.sample_basic_plugins),

    LATEX(R.string.sample_latex),

    CUSTOM_EXTENSION(R.string.sample_custom_extension),

    RECYCLER(R.string.sample_recycler),

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
