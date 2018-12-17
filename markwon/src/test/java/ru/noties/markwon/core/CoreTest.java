package ru.noties.markwon.core;

import android.support.annotation.NonNull;
import android.text.Spanned;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonConfiguration;
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

        final Spanned spanned = (Spanned) Markwon.builder(RuntimeEnvironment.application)
                .use(CorePlugin.create())
                .use(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                        builder.factory(new MarkwonSpannableFactoryDef() {

                            @Override
                            public Object strongEmphasis() {
                                return span("bold");
                            }

                            @Override
                            public Object emphasis() {
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
