package io.noties.markwon.image.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DataUriParserTest {

    private DataUriParser.Impl impl;

    @Before
    public void before() {
        impl = new DataUriParser.Impl();
    }

    @Test
    public void test() {

        final Map<String, DataUri> data = new LinkedHashMap<String, DataUri>() {{
            put(",", new DataUri(null, false, null));
            put("image/svg+xml;base64,!@#$%^&*(", new DataUri("image/svg+xml", true, "!@#$%^&*("));
            put("text/vnd-example+xyz;foo=bar;base64,R0lGODdh", new DataUri("text/vnd-example+xyz", true, "R0lGODdh"));
            put("text/plain;charset=UTF-8;page=21,the%20data:1234,5678", new DataUri("text/plain", false, "the%20data:1234,5678"));
        }};

        for (Map.Entry<String, DataUri> entry : data.entrySet()) {
            assertEquals(entry.getKey(), entry.getValue(), impl.parse(entry.getKey()));
        }
    }

    @Test
    public void data_new_lines_are_ignored() {

        final String input = "image/png;base64,iVBORw0KGgoAAA\n" +
                "ANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4\n" +
                "//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU\n" +
                "5ErkJggg==";

        assertEquals(
                new DataUri("image/png", true, "iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg=="),
                impl.parse(input)
        );
    }

    @Test
    public void no_comma_returns_null() {

        final String[] inputs = {
                "",
                "what-ever",
                ";;;;;;;",
                "some crazy data"
        };

        for (String input : inputs) {
            assertNull(input, impl.parse(input));
        }
    }

    @Test
    public void two_commas() {
        final String input = ",,"; // <- second one would be considered data...
        assertEquals(
                input,
                new DataUri(null, false, ","),
                impl.parse(input)
        );
    }

    @Test
    public void more_commas() {
        final String input = "first,second,third"; // <- first is just a value (will be ignored)
        assertEquals(
                input,
                new DataUri(null, false, "second,third"),
                impl.parse(input)
        );
    }

    @Test
    public void base64_no_content_type() {
        final String input = ";base64,12345";
        assertEquals(
                input,
                new DataUri(null, true, "12345"),
                impl.parse(input)
        );
    }

    @Test
    public void not_base64_no_content_type() {
        final String input = ",qweRTY";
        assertEquals(
                input,
                new DataUri(null, false, "qweRTY"),
                impl.parse(input)
        );
    }

    @Test
    public void content_type_data_no_base64() {
        final String input = "image/png,aSdFg";
        assertEquals(
                input,
                new DataUri("image/png", false, "aSdFg"),
                impl.parse(input)
        );
    }
}