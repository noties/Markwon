package ru.noties.markwon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.priority.Priority;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AbstractMarkwonPluginTest {

    @Test
    public void priority() {
        // returns CorePlugin dependency

        final Priority priority = new AbstractMarkwonPlugin() {
        }.priority();
        final List<Class<? extends MarkwonPlugin>> after = priority.after();
        assertEquals(1, after.size());
        assertEquals(CorePlugin.class, after.get(0));
    }

    @Test
    public void process_markdown() {
        // returns supplied argument (no-op)

        final String[] input = {
                "hello",
                "!\nworld___-976"
        };

        for (String s : input) {
            assertEquals(s, new AbstractMarkwonPlugin() {
            }.processMarkdown(s));
        }
    }
}