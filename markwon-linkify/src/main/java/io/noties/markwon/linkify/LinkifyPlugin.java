package io.noties.markwon.linkify;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.text.util.LinkifyCompat;

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
        return create(false);
    }

    /**
     * @param useCompat If true, use {@link LinkifyCompat} to handle links.
     *                  Note that the {@link LinkifyCompat} depends on androidx.core:core,
     *                  the dependency must be added on a client side explicitly.
     * @since 4.3.0 `useCompat` argument
     */
    @NonNull
    public static LinkifyPlugin create(boolean useCompat) {
        return create(Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS | Linkify.WEB_URLS, useCompat);
    }

    @NonNull
    public static LinkifyPlugin create(@LinkifyMask int mask) {
        return new LinkifyPlugin(mask, false);
    }

    /**
     * @param useCompat If true, use {@link LinkifyCompat} to handle links.
     *                  Note that the {@link LinkifyCompat} depends on androidx.core:core,
     *                  the dependency must be added on a client side explicitly.
     * @since 4.3.0 `useCompat` argument
     */
    @NonNull
    public static LinkifyPlugin create(@LinkifyMask int mask, boolean useCompat) {
        return new LinkifyPlugin(mask, useCompat);
    }

    private final int mask;
    private final boolean useCompat;

    @SuppressWarnings("WeakerAccess")
    LinkifyPlugin(@LinkifyMask int mask, boolean useCompat) {
        this.mask = mask;
        this.useCompat = useCompat;
    }

    @Override
    public void configure(@NonNull Registry registry) {
        registry.require(CorePlugin.class, new Action<CorePlugin>() {
            @Override
            public void apply(@NonNull CorePlugin corePlugin) {
                final LinkifyTextAddedListener listener;
                // @since 4.3.0
                if (useCompat) {
                    listener = new LinkifyCompatTextAddedListener(mask);
                } else {
                    listener = new LinkifyTextAddedListener(mask);
                }
                corePlugin.addOnTextAddedListener(listener);
            }
        });
    }

    private static class LinkifyTextAddedListener implements CorePlugin.OnTextAddedListener {

        private final int mask;

        LinkifyTextAddedListener(int mask) {
            this.mask = mask;
        }

        @Override
        public void onTextAdded(@NonNull MarkwonVisitor visitor, @NonNull String text, int start) {

            // @since 4.2.0 obtain span factory for links
            //  we will be using the link that is used by markdown (instead of directly applying URLSpan)
            final SpanFactory spanFactory = visitor.configuration().spansFactory().get(Link.class);
            if (spanFactory == null) {
                return;
            }

            // @since 4.2.0 we no longer re-use builder (thread safety achieved for
            //  render calls from different threads and ... better performance)
            final SpannableStringBuilder builder = new SpannableStringBuilder(text);

            if (addLinks(builder, mask)) {
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

        protected boolean addLinks(@NonNull Spannable text, @LinkifyMask int mask) {
            return Linkify.addLinks(text, mask);
        }
    }

    // @since 4.3.0
    private static class LinkifyCompatTextAddedListener extends LinkifyTextAddedListener {

        LinkifyCompatTextAddedListener(int mask) {
            super(mask);
        }

        @Override
        protected boolean addLinks(@NonNull Spannable text, @LinkifyMask int mask) {
            return LinkifyCompat.addLinks(text, mask);
        }
    }
}
