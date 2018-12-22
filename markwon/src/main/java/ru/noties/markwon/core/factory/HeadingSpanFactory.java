package ru.noties.markwon.core.factory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.core.CoreProps;
import ru.noties.markwon.core.spans.HeadingSpan;

public class HeadingSpanFactory implements SpanFactory {
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps context) {
        return new HeadingSpan(
                configuration.theme(),
                CoreProps.HEADING_LEVEL.require(context)
        );
    }
}
