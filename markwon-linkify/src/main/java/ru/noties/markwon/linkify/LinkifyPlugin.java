package ru.noties.markwon.linkify;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.util.Linkify;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.core.CorePlugin;

public class LinkifyPlugin extends AbstractMarkwonPlugin {

    @IntDef(flag = true, value = {
            Linkify.EMAIL_ADDRESSES,
            Linkify.PHONE_NUMBERS,
            Linkify.WEB_URLS,
            Linkify.ALL
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

    private static class LinkifyTextAddedListener implements CorePlugin.OnTextAddedListener {

        private final int mask;
        private final SpannableStringBuilder builder;

        LinkifyTextAddedListener(int mask) {
            this.mask = mask;
            this.builder = new SpannableStringBuilder();
        }

        @Override
        public void onTextAdded(@NonNull MarkwonVisitor visitor, @NonNull String text, int start) {

            // clear previous state
            builder.clear();
            builder.clearSpans();

            // append text to process
            builder.append(text);

            if (Linkify.addLinks(builder, mask)) {
                final Object[] spans = builder.getSpans(0, builder.length(), Object.class);
                if (spans != null
                        && spans.length > 0) {
                    final SpannableBuilder spannableBuilder = visitor.builder();
                    for (Object span : spans) {
                        spannableBuilder.setSpan(
                                span,
                                start + builder.getSpanStart(span),
                                start + builder.getSpanEnd(span),
                                builder.getSpanFlags(span));
                    }
                }
            }
        }
    }
}
