package ru.noties.markwon.ext.tasklist;

import android.support.annotation.NonNull;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonSpansFactory;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.test.TestSpan;
import ru.noties.markwon.test.TestSpanMatcher;

import static ru.noties.markwon.test.TestSpan.args;
import static ru.noties.markwon.test.TestSpan.document;
import static ru.noties.markwon.test.TestSpan.span;
import static ru.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TaskListTest {

    private static final String SPAN = "task-list";
    private static final String IS_DONE = "is-done";

    @Test
    public void test() {

        final TestSpan.Document document = document(
                span(SPAN, args(IS_DONE, false), text("First")),
                newLine(),
                span(SPAN, args(IS_DONE, true), text("Second")),
                newLine(),
                span(SPAN, args(IS_DONE, true), text("Third")),
                newLine(),
                span(SPAN, args(IS_DONE, false), text("First star")),
                newLine(),
                span(SPAN, args(IS_DONE, true), text("Second star")),
                newLine(),
                span(SPAN, args(IS_DONE, true), text("Third star")),
                newLine(),
                span(SPAN, args(IS_DONE, false), text("First plus")),
                newLine(),
                span(SPAN, args(IS_DONE, true), text("Second plus")),
                newLine(),
                span(SPAN, args(IS_DONE, true), text("Third plus")),
                newLine(),
                span(SPAN, args(IS_DONE, true), text("Number with dot")),
                newLine(),
                span(SPAN, args(IS_DONE, false), text("Number"))
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
                                return span(SPAN, args(IS_DONE, TaskListProps.DONE.require(props)));
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
        return text("\n");
    }
}
