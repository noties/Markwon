package io.noties.markwon.core.factory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.noties.markwon.core.CoreProps;
import io.noties.markwon.core.spans.LinkSpan;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;

public class LinkSpanFactory implements SpanFactory {
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return new LinkSpan(
                configuration.theme(),
                CoreProps.LINK_DESTINATION.require(props),
                configuration.linkResolver()
        );
    }
}
