package ru.noties.markwon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AbstractMarkwonPluginTest {

    @Test
    public void priority() {
        // returns CorePlugin dependency

        fail();
    }

    @Test
    public void process_markdown() {
        // returns supplied argument (no-op)

        fail();
    }
}