package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;

import org.commonmark.node.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collection;

import ix.Ix;
import ix.IxPredicate;
import ru.noties.markwon.LinkResolverDef;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.SpannableFactory;
import ru.noties.markwon.html.api.MarkwonHtmlParser;
import ru.noties.markwon.renderer.SpannableMarkdownVisitor;
import ru.noties.markwon.spans.SpannableTheme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

        final SpannableStringBuilder stringBuilder = builder.spannableStringBuilder();

        System.out.printf("%n%s%n", stringBuilder);

        int index = 0;

        for (TestNode testNode : data.output()) {
            index = validate(stringBuilder, index, testNode);
        }
    }

    private int validate(@NonNull SpannableStringBuilder builder, int index, @NonNull TestNode node) {

        if (node.isText()) {

            final String text;
            {
                final String content = node.getAsText().text();

                // code is a special case as we wrap it around non-breakable spaces
                final TestNode parent = node.parent();
                if (parent != null) {
                    final TestNode.Span span = parent.getAsSpan();
                    if (TestSpan.CODE.equals(span.name())) {
                        text = "\u00a0" + content + "\u00a0";
                    } else if (TestSpan.CODE_BLOCK.equals(span.name())) {
                        text = "\u00a0\n" + content + "\n\u00a0";
                    } else {
                        text = content;
                    }
                } else {
                    text = content;
                }
            }

            assertEquals(text, builder.subSequence(index, index + text.length()).toString());

            return index + text.length();
        }

        final TestNode.Span span = node.getAsSpan();

        int out = index;

        for (TestNode child : span.children()) {
            out = validate(builder, out, child);
        }

        final String info = node.toString();

        // we can possibly have parent spans here, should filter them
        final Object[] spans = builder.getSpans(index, out, Object.class);
        assertTrue(info, spans != null);

        final TestSpan testSpan = Ix.fromArray(spans)
                .filter(new IxPredicate<Object>() {
                    @Override
                    public boolean test(Object o) {
                        return o instanceof TestSpan;
                    }
                })
                .cast(TestSpan.class)
                .filter(new IxPredicate<TestSpan>() {
                    @Override
                    public boolean test(TestSpan testSpan) {
                        return span.name().equals(testSpan.name());
                    }
                })
                .first(null);

        assertNotNull(info, testSpan);

        assertEquals(info, span.name(), testSpan.name());
        assertEquals(info, span.attributes(), testSpan.attributes());

        return out;
    }

    @NonNull
    private SpannableConfiguration configuration(@NonNull TestConfig config) {

        final SpannableFactory factory = new TestFactory(config.hasOption(TestConfig.USE_PARAGRAPHS));
        final MarkwonHtmlParser htmlParser = config.hasOption(TestConfig.USE_HTML)
                ? null
                : MarkwonHtmlParser.noOp();

        // todo: rest omitted for now
        return SpannableConfiguration.builder(null)
                .theme(mock(SpannableTheme.class))
                .linkResolver(mock(LinkResolverDef.class))
                .htmlParser(htmlParser)
                .factory(factory)
                .build();
    }
}