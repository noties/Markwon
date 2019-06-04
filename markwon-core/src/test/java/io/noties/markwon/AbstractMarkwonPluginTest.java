package io.noties.markwon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AbstractMarkwonPluginTest {

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