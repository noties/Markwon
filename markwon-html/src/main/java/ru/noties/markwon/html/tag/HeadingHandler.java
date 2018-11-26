package ru.noties.markwon.html.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.html.HtmlTag;

public class HeadingHandler extends SimpleTagHandler {

    private final int level;

    public HeadingHandler(int level) {
        this.level = level;
    }

    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull HtmlTag tag) {
        return configuration.factory().heading(configuration.theme(), level);
    }
}
