package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

import org.commonmark.node.Node;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

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

//        System.out.printf("%s: %s%n", file, Arrays.toString(stringBuilder.getSpans(0, stringBuilder.length(), Object.class)));

        int index = 0;

        for (TestNode testNode : data.output()) {
            index = validate(stringBuilder, index, testNode);
        }

        // assert that the whole thing is processed
        assertEquals(stringBuilder.length(), index);
    }

    private int validate(@NonNull final SpannableStringBuilder builder, final int index, @NonNull TestNode node) {

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

        final int end = out;

        final String info = node.toString();

//        System.out.printf("%s: %s%n", file, builder.subSequence(index, out));

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
                        return span.name().equals(testSpan.name())
                                && index == builder.getSpanStart(testSpan)
                                && end == builder.getSpanEnd(testSpan);
                    }
                })
                .first(null);

        assertNotNull(
                format("info: %s, spans: %s", info, Arrays.toString(spans)),
                testSpan
        );

        assertEquals(info, span.name(), testSpan.name());

        // for correct tracking of nested blocks we must validate expected start/end
        assertEquals(info, index, builder.getSpanStart(testSpan));
        assertEquals(info, out, builder.getSpanEnd(testSpan));

        System.out.printf("%s: expected: %s, actual: %s%n", file, span.attributes(), testSpan.attributes());

        assertMapEquals(info, span.attributes(), testSpan.attributes());

        return out;
    }

    @SuppressWarnings("ConstantConditions")
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

    private static void assertMapEquals(
            @NonNull String message,
            @NonNull Map<String, String> expected,
            @NonNull Map<String, String> actual) {
        boolean result = expected.size() == actual.size();
        if (result) {
            for (Map.Entry<String, String> entry : expected.entrySet()) {
                if (!actual.containsKey(entry.getKey())
                        || !equals(entry.getValue(), actual.get(entry.getKey()))) {
                    result = false;
                    break;
                }
            }
        }
        if (!result) {
            final Comparator<Map.Entry<String, String>> comparator = new Comparator<Map.Entry<String, String>>() {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            };
            final String e = Ix.from(expected.entrySet())
                    .orderBy(comparator)
                    .toList()
                    .toString();
            final String a = Ix.from(actual.entrySet())
                    .orderBy(comparator)
                    .toList()
                    .toString();
            throw new ComparisonFailure(message, e, a);
        }
    }

    private static boolean equals(@Nullable Object o1, @Nullable Object o2) {
        return o1 != null
                ? o1.equals(o2)
                : o2 == null;

    }

    @NonNull
    private static String format(@NonNull String message, Object... args) {
        return String.format(message, args);
    }
}