package io.noties.markwon.linkify;

import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import org.commonmark.node.Link;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.core.CoreProps;

public class LinkifyPlugin extends AbstractMarkwonPlugin {

    @IntDef(flag = true, value = {
            Linkify.EMAIL_ADDRESSES,
            Linkify.PHONE_NUMBERS,
            Linkify.WEB_URLS
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface LinkifyMask {
    }

    @NonNull
    public static LinkifyPlugin create() {
        return create(Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS | Linkify.WEB_URLS);
    }

    @NonNull
    public static LinkifyPlugin create(@LinkifyMask int mask) {
        return new LinkifyPlugin(mask);
    }

    private final int mask;

    @SuppressWarnings("WeakerAccess")
    LinkifyPlugin(@LinkifyMask int mask) {
        this.mask = mask;
    }

    @Override
    public void configure(@NonNull Registry registry) {
        registry.require(CorePlugin.class, new Action<CorePlugin>() {
            @Override
            public void apply(@NonNull CorePlugin corePlugin) {
                corePlugin.addOnTextAddedListener(new LinkifyTextAddedListener(mask));
            }
        });
    }

    // todo: thread safety (builder is reused)
    private static class LinkifyTextAddedListener implements CorePlugin.OnTextAddedListener {

        private final int mask;
        private final SpannableStringBuilder builder;

        LinkifyTextAddedListener(int mask) {
            this.mask = mask;
            this.builder = new SpannableStringBuilder();
        }

        @Override
        public void onTextAdded(@NonNull MarkwonVisitor visitor, @NonNull String text, int start) {

            // @since 4.2.0-SNAPSHOT obtain span factory for links
            //  we will be using the link that is used by markdown (instead of directly applying URLSpan)
            final SpanFactory spanFactory = visitor.configuration().spansFactory().get(Link.class);
            if (spanFactory == null) {
                return;
            }

            // clear previous state
            builder.clear();
            builder.clearSpans();

            // append text to process
            builder.append(text);

            if (Linkify.addLinks(builder, mask)) {
                // target URL span specifically
                final URLSpan[] spans = builder.getSpans(0, builder.length(), URLSpan.class);
                if (spans != null
                        && spans.length > 0) {

                    final RenderProps renderProps = visitor.renderProps();
                    final SpannableBuilder spannableBuilder = visitor.builder();

                    for (URLSpan span : spans) {
                        CoreProps.LINK_DESTINATION.set(renderProps, span.getURL());
                        SpannableBuilder.setSpans(
                                spannableBuilder,
                                spanFactory.getSpans(visitor.configuration(), renderProps),
                                start + builder.getSpanStart(span),
                                start + builder.getSpanEnd(span)
                        );
                    }
                }
            }
        }
    }
}
