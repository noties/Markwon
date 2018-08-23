package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;

import org.commonmark.node.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

import ix.Ix;
import ix.IxPredicate;
import ru.noties.markwon.LinkResolverDef;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.SpannableFactory;
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
        final String raw = stringBuilder.toString();

        int index = 0;
        int lastIndex = 0;

        for (TestEntry entry : data.output()) {

            final String expected = entry.text();

            final boolean isText = "text".equals(entry.name());

            final int start;
            final int end;

            if (isText) {
                start = lastIndex;
                end = start + expected.length();
                index = lastIndex = end;
            } else {
                start = raw.indexOf(expected, index);
                if (start < 0) {
                    throw new AssertionError(String.format("Cannot find `%s` starting at index: %d, raw: %n###%n%s%n###",
                            expected, start, raw
                    ));
                }
                end = start + expected.length();
                lastIndex = Math.max(end, lastIndex);
            }

            if (!expected.equals(raw.substring(start, end))) {
                throw new AssertionError(String.format("Expected: `%s`, actual: `%s`, start: %d, raw: %n###%n%s%n###",
                        expected, raw.substring(start, end), start, raw
                ));
            }

            final Object[] spans = stringBuilder.getSpans(start, end, Object.class);
            final int length = spans != null ? spans.length : 0;

            if (isText) {
                // validate no spans
                assertEquals(Arrays.toString(spans), 0, length);
            } else {
                assertTrue(length > 0);
                final Object span = Ix.fromArray(spans)
                        .filter(new IxPredicate<Object>() {
                            @Override
                            public boolean test(Object o) {
                                return start == stringBuilder.getSpanStart(o)
                                        && end == stringBuilder.getSpanEnd(o);
                            }
                        })
                        .first(null);
                assertNotNull(span);
                assertTrue(span instanceof TestSpan);
                final TestSpan testSpan = (TestSpan) span;
                assertEquals(entry.name(), testSpan.name());
                assertEquals(entry.attributes(), testSpan.attributes());
            }
        }
    }

    @NonNull
    private SpannableConfiguration configuration(@NonNull TestConfig config) {

        final SpannableFactory factory = new TestFactory(config.hasOption(TestConfig.USE_PARAGRAPHS));

        // todo: rest omitted for now
        return SpannableConfiguration.builder(null)
                .theme(mock(SpannableTheme.class))
                .linkResolver(mock(LinkResolverDef.class))
                .factory(factory)
                .build();

//        return configuration;
    }
}