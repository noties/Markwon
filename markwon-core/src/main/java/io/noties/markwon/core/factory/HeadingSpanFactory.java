package io.noties.markwon.core.factory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.noties.markwon.core.CoreProps;
import io.noties.markwon.core.spans.HeadingSpan;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;

public class HeadingSpanFactory implements SpanFactory {
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return new HeadingSpan(
                configuration.theme(),
                CoreProps.HEADING_LEVEL.require(props)
        );
    }
}
