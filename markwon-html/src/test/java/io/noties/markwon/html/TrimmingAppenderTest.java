package io.noties.markwon.html;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TrimmingAppenderTest {

    private TrimmingAppender.Impl impl;

    @Before
    public void before() {
        impl = new TrimmingAppender.Impl();
    }

    @Test
    public void singlePart() {
        final String input = "        html  body \n\ndiv         hey      ";
        final StringBuilder builder = new StringBuilder();
        impl.append(builder, input);
        assertEquals("html body div hey ", builder.toString());
    }

    @Test
    public void multiParts() {
        final String[] inputs = {
                "\n\n\n\n\nhtml\t   body\n\ndiv ",
                "      span   and go"
        };
        final StringBuilder builder = new StringBuilder();
        for (String input : inputs) {
            impl.append(builder, input);
        }

        assertEquals("html body div span and go", builder.toString());
    }
}