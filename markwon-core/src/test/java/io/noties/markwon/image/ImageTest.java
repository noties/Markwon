package io.noties.markwon.image;

import android.content.Context;
import androidx.annotation.NonNull;

import org.commonmark.node.Image;
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
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.test.TestSpan.Document;
import io.noties.markwon.test.TestSpanMatcher;

import static io.noties.markwon.test.TestSpan.args;
import static io.noties.markwon.test.TestSpan.document;
import static io.noties.markwon.test.TestSpan.span;
import static io.noties.markwon.test.TestSpan.text;

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
