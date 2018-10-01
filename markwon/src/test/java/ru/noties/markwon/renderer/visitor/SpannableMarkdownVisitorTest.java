package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;

import org.commonmark.node.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

import ru.noties.markwon.LinkResolverDef;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.SpannableFactory;
import ru.noties.markwon.html.api.MarkwonHtmlParser;
import ru.noties.markwon.renderer.SpannableMarkdownVisitor;
import ru.noties.markwon.spans.SpannableTheme;

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

        final SpannableConfiguration configuration = configuration(data.config());
        final SpannableBuilder builder = new SpannableBuilder();
        final SpannableMarkdownVisitor visitor = new SpannableMarkdownVisitor(configuration, builder);
        final Node node = Markwon.createParser().parse(data.input());
        node.accept(visitor);


        final TestValidator validator = TestValidator.create(file);

        int index = 0;

        for (TestNode testNode : data.output()) {
            index = validator.validate(builder, index, testNode);
        }

        // assert that the whole thing is processed
        assertEquals("`" + builder + "`", builder.length(), index);

        final Object[] spans = builder.getSpans(0, builder.length(), Object.class);
        final int length = spans != null
                ? spans.length
                : 0;

        assertEquals(Arrays.toString(spans), validator.processedSpanNodesCount(), length);
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    private SpannableConfiguration configuration(@NonNull TestConfig config) {

        final SpannableFactory factory = new TestFactory(config.hasOption(TestConfig.USE_PARAGRAPHS));
        final MarkwonHtmlParser htmlParser = config.hasOption(TestConfig.USE_HTML)
                ? null
                : MarkwonHtmlParser.noOp();

        final boolean softBreakAddsNewLine = config.hasOption(TestConfig.SOFT_BREAK_ADDS_NEW_LINE);
        final boolean htmlAllowNonClosedTags = config.hasOption(TestConfig.HTML_ALLOW_NON_CLOSED_TAGS);

        return SpannableConfiguration.builder(null)
                .theme(mock(SpannableTheme.class))
                .linkResolver(mock(LinkResolverDef.class))
                .htmlParser(htmlParser)
                .factory(factory)
                .softBreakAddsNewLine(softBreakAddsNewLine)
                .htmlAllowNonClosedTags(htmlAllowNonClosedTags)
                .build();
    }
}