package ru.noties.markwon.core;

import android.support.annotation.NonNull;
import android.text.Spanned;

import org.commonmark.node.Emphasis;
import org.commonmark.node.StrongEmphasis;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonSpansFactory;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.test.TestSpan;
import ru.noties.markwon.test.TestSpanMatcher;

import static ru.noties.markwon.test.TestSpan.document;
import static ru.noties.markwon.test.TestSpan.span;
import static ru.noties.markwon.test.TestSpan.text;

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
