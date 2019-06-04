package ru.noties.markwon.html.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.html.HtmlTag;
import ru.noties.markwon.html.span.SuperScriptSpan;

public class SuperScriptHandler extends SimpleTagHandler {
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps renderProps, @NonNull HtmlTag tag) {
        return new SuperScriptSpan();
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Collections.singleton("sup");
    }
}
