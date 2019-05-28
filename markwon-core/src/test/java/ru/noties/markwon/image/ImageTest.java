package ru.noties.markwon.image;

import android.content.Context;
import android.support.annotation.NonNull;

import org.commonmark.node.Image;
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
import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.test.TestSpan.Document;
import ru.noties.markwon.test.TestSpanMatcher;

import static ru.noties.markwon.test.TestSpan.args;
import static ru.noties.markwon.test.TestSpan.document;
import static ru.noties.markwon.test.TestSpan.span;
import static ru.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ImageTest {

    @Test
    public void test() {

        final String markdown = "![alt](#href)";

        final Context context = RuntimeEnvironment.application;
        final Markwon markwon = Markwon.builder(context)
                .usePlugin(CorePlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder.setFactory(Image.class, new SpanFactory() {
                            @Override
                            public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                                return span("image", args("href", ImageProps.DESTINATION.require(props)));
                            }
                        });
                    }
                })
                .build();

        final Document document = document(
                span("image", args("href", "#href"), text("alt"))
        );

        TestSpanMatcher.matches(markwon.toMarkdown(markdown), document);
    }
}
