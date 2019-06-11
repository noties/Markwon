package io.noties.markwon.core;

import androidx.annotation.NonNull;
import android.text.Spanned;

import org.commonmark.node.Emphasis;
import org.commonmark.node.StrongEmphasis;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.test.TestSpan;
import io.noties.markwon.test.TestSpanMatcher;

import static io.noties.markwon.test.TestSpan.document;
import static io.noties.markwon.test.TestSpan.span;
import static io.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CoreTest {

    @Test
    public void bold_italic() {

        final String input = "**_bold italic_**";
        final TestSpan.Document document = document(
                span("bold",
                        span("italic", text("bold italic"))));

        final Spanned spanned = Markwon.builder(RuntimeEnvironment.application)
                .usePlugin(CorePlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder
                                .setFactory(StrongEmphasis.class, new SpanFactory() {
                                    @Override
                                    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                                        return span("bold");
                                    }
                                })
                                .setFactory(Emphasis.class, new SpanFactory() {
                                    @Override
                                    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                                        return span("italic");
                                    }
                                });
                    }
                })
                .build()
                .toMarkdown(input);

        TestSpanMatcher.matches(spanned, document);
    }
}
