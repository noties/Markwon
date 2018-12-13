package ru.noties.markwon.renderer.visitor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.image.ImagesPlugin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SpannableMarkdownVisitorTest {

    @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
    public static Collection<Object> parameters() {
        return TestDataReader.testFiles();
    }

    private final String file;

    public SpannableMarkdownVisitorTest(@NonNull String file) {
        this.file = file;
    }

    @Test
    public void test() {

        final TestData data = TestDataReader.readTest(file);
        
        final Markwon markwon = markwon(data.config());

        // okay we must thing about it... casting?
        final SpannableStringBuilder stringBuilder = (SpannableStringBuilder) markwon.toMarkdown(data.input());

        final TestValidator validator = TestValidator.create(file);

        int index = 0;

        for (TestNode testNode : data.output()) {
            index = validator.validate(stringBuilder, index, testNode);
        }

        // assert that the whole thing is processed
        assertEquals("`" + stringBuilder + "`", stringBuilder.length(), index);

        final Object[] spans = stringBuilder.getSpans(0, stringBuilder.length(), Object.class);
        final int length = spans != null
                ? spans.length
                : 0;

        assertEquals(Arrays.toString(spans), validator.processedSpanNodesCount(), length);
    }


    @NonNull
    private Markwon markwon(@NonNull final TestConfig config) {
        return Markwon.builder(RuntimeEnvironment.application)
                .use(CorePlugin.create(config.hasOption(TestConfig.SOFT_BREAK_ADDS_NEW_LINE)))
                .use(ImagesPlugin.create(mock(Context.class)))
                .use(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                        builder.factory(new TestFactory(config.hasOption(TestConfig.USE_PARAGRAPHS)));
                    }
                })
                .build();
    }
}