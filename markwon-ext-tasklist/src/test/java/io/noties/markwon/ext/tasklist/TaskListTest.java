package io.noties.markwon.ext.tasklist;

import androidx.annotation.NonNull;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.test.TestSpan;
import io.noties.markwon.test.TestSpanMatcher;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TaskListTest {

    private static final String SPAN = "task-list";
    private static final String IS_DONE = "is-done";

    @Test
    public void test() {

        // NB! different markers lead to different types of lists,
        //  that's why there are 2 new lines after each type

        final TestSpan.Document document = TestSpan.document(
                TestSpan.span(SPAN, TestSpan.args(IS_DONE, false), TestSpan.text("First")),
                newLine(),
                TestSpan.span(SPAN, TestSpan.args(IS_DONE, true), TestSpan.text("Second")),
                newLine(),
                TestSpan.span(SPAN, TestSpan.args(IS_DONE, true), TestSpan.text("Third")),
                newLine(),
                newLine(),
                TestSpan.span(SPAN, TestSpan.args(IS_DONE, false), TestSpan.text("First star")),
                newLine(),
                TestSpan.span(SPAN, TestSpan.args(IS_DONE, true), TestSpan.text("Second star")),
                newLine(),
                TestSpan.span(SPAN, TestSpan.args(IS_DONE, true), TestSpan.text("Third star")),
                newLine(),
                newLine(),
                TestSpan.span(SPAN, TestSpan.args(IS_DONE, false), TestSpan.text("First plus")),
                newLine(),
                TestSpan.span(SPAN, TestSpan.args(IS_DONE, true), TestSpan.text("Second plus")),
                newLine(),
                TestSpan.span(SPAN, TestSpan.args(IS_DONE, true), TestSpan.text("Third plus")),
                newLine(),
                newLine(),
                TestSpan.span(SPAN, TestSpan.args(IS_DONE, true), TestSpan.text("Number with dot")),
                newLine(),
                newLine(),
                TestSpan.span(SPAN, TestSpan.args(IS_DONE, false), TestSpan.text("Number"))
        );

        TestSpanMatcher.matches(
                markwon().toMarkdown(read("task-lists.md")),
                document
        );
    }

    @NonNull
    private static Markwon markwon() {
        return Markwon.builder(RuntimeEnvironment.application)
                .usePlugin(TaskListPlugin.create(RuntimeEnvironment.application))
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder.setFactory(TaskListItem.class, new SpanFactory() {
                            @Override
                            public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                                return TestSpan.span(SPAN, TestSpan.args(IS_DONE, TaskListProps.DONE.require(props)));
                            }
                        });
                    }
                })
                .build();
    }

    @SuppressWarnings("SameParameterValue")
    @NonNull
    private static String read(@NonNull String name) {
        try {
            return IOUtils.resourceToString("tests/" + name, StandardCharsets.UTF_8, TaskListDrawable.class.getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private static TestSpan.Text newLine() {
        return TestSpan.text("\n");
    }
}
