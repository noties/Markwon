package io.noties.markwon.sample;

import androidx.annotation.StringRes;

public enum Sample {

    // all usages of markwon without plugins (parse, render, setMarkwon, etc)
    CORE(R.string.sample_core),

    BASIC_PLUGINS(R.string.sample_basic_plugins),

    LATEX(R.string.sample_latex),

    CUSTOM_EXTENSION(R.string.sample_custom_extension),

    RECYCLER(R.string.sample_recycler),

    HTML(R.string.sample_html),

    SIMPLE_EXT(R.string.sample_simple_ext);

    private final int textResId;

    Sample(@StringRes int textResId) {
        this.textResId = textResId;
    }

    @StringRes
    public int textResId() {
        return textResId;
    }
}
