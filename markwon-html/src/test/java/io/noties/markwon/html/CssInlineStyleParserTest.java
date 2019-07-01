package io.noties.markwon.html;

import androidx.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ix.Ix;
import ix.IxFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CssInlineStyleParserTest {

    private CssInlineStyleParser.Impl impl;

    @Before
    public void before() {
        impl = new CssInlineStyleParser.Impl();
    }

    @Test
    public void simple_single_pair() {

        final String input = "key: value;";

        final List<CssProperty> list = listProperties(input);

        assertEquals(1, list.size());

        with(list.get(0), new Action<CssProperty>() {
            @Override
            public void apply(@NonNull CssProperty cssProperty) {
                assertEquals("key", cssProperty.key());
                assertEquals("value", cssProperty.value());
            }
        });
    }

    @Test
    public void simple_two_pairs() {

        final String input = "key1: value1; key2: value2;";

        final List<CssProperty> list = listProperties(input);

        assertEquals(2, list.size());

        with(list.get(0), new Action<CssProperty>() {
            @Override
            public void apply(@NonNull CssProperty cssProperty) {
                assertEquals("key1", cssProperty.key());
                assertEquals("value1", cssProperty.value());
            }
        });

        with(list.get(1), new Action<CssProperty>() {
            @Override
            public void apply(@NonNull CssProperty cssProperty) {
                assertEquals("key2", cssProperty.key());
                assertEquals("value2", cssProperty.value());
            }
        });
    }

    @Test
    public void one_pair_eof() {

        final String input = "key: value";
        final List<CssProperty> list = listProperties(input);
        assertEquals(1, list.size());

        with(list.get(0), new Action<CssProperty>() {
            @Override
            public void apply(@NonNull CssProperty cssProperty) {
                assertEquals("key", cssProperty.key());
                assertEquals("value", cssProperty.value());
            }
        });
    }

    @Test
    public void one_pair_eof_whitespaces() {

        final String input = "key: value         \n\n\t";
        final List<CssProperty> list = listProperties(input);
        assertEquals(1, list.size());

        with(list.get(0), new Action<CssProperty>() {
            @Override
            public void apply(@NonNull CssProperty cssProperty) {
                assertEquals("key", cssProperty.key());
                assertEquals("value", cssProperty.value());
            }
        });
    }

    @Test
    public void white_spaces() {

        final String input = "\n\n\n\t    \t key1 \n\n\n\t  : \n\n\n\n   \t value1    \n\n\n\n    ; \n key2\n : \n value2 \n ; ";
        final List<CssProperty> list = listProperties(input);
        assertEquals(2, list.size());

        with(list.get(0), new Action<CssProperty>() {
            @Override
            public void apply(@NonNull CssProperty cssProperty) {
                assertEquals("key1", cssProperty.key());
                assertEquals("value1", cssProperty.value());
            }
        });

        with(list.get(1), new Action<CssProperty>() {
            @Override
            public void apply(@NonNull CssProperty cssProperty) {
                assertEquals("key2", cssProperty.key());
                assertEquals("value2", cssProperty.value());
            }
        });
    }

    @Test
    public void list_of_keys() {

        final String input = "key1 key2 key3 key4";
        final List<CssProperty> list = listProperties(input);

        assertEquals(0, list.size());
    }

    @Test
    public void list_of_keys_and_value() {

        final String input = "key1 key2 key3 key4: value4";
        final List<CssProperty> list = listProperties(input);
        assertEquals(1, list.size());

        with(list.get(0), new Action<CssProperty>() {
            @Override
            public void apply(@NonNull CssProperty cssProperty) {
                assertEquals("key4", cssProperty.key());
                assertEquals("value4", cssProperty.value());
            }
        });
    }

    @Test
    public void list_of_keys_separated_by_semi_colon() {

        final String input = "key1;key2;key3;key4;";
        final List<CssProperty> list = listProperties(input);
        assertEquals(0, list.size());
    }

    @Test
    public void key_value_with_invalid_between() {

        final String input = "key1: value1; key2 key3: value3;";
        final List<CssProperty> list = listProperties(input);

        assertEquals(2, list.size());

        with(list.get(0), new Action<CssProperty>() {
            @Override
            public void apply(@NonNull CssProperty cssProperty) {
                assertEquals("key1", cssProperty.key());
                assertEquals("value1", cssProperty.value());
            }
        });

        with(list.get(1), new Action<CssProperty>() {
            @Override
            public void apply(@NonNull CssProperty cssProperty) {
                assertEquals("key3", cssProperty.key());
                assertEquals("value3", cssProperty.value());
            }
        });
    }

    @Test
    public void css_functions() {

        final Map<String, String> map = new HashMap<String, String>() {{
            put("attr", "\" (\" attr(href) \")\"");
            put("calc", "calc(100% - 100px)");
            put("cubic-bezier", "cubic-bezier(0.1, 0.7, 1.0, 0.1)");
            put("hsl", "hsl(120,100%,50%)");
            put("hsla", "hsla(120,100%,50%,0.3)");
            put("linear-gradient", "linear-gradient(red, yellow, blue)");
            put("radial-gradient", "radial-gradient(red, green, blue)");
            put("repeating-linear-gradient", "repeating-linear-gradient(red, yellow 10%, green 20%)");
            put("repeating-radial-gradient", "repeating-radial-gradient(red, yellow 10%, green 15%)");
            put("rgb", "rgb(255,0,0)");
            put("rgba", "rgba(255,0,0,0.3)");
            put("var", "var(--some-variable)");
            put("url", "url(\"url.gif\")");
        }};

        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(entry.getKey())
                    .append(':')
                    .append(entry.getValue())
                    .append(';');
        }

        for (CssProperty cssProperty : impl.parse(builder.toString())) {
            final String value = map.remove(cssProperty.key());
            assertNotNull(cssProperty.key(), value);
            assertEquals(cssProperty.key(), value, cssProperty.value());
        }

        assertEquals(0, map.size());
    }

    @NonNull
    private List<CssProperty> listProperties(@NonNull String input) {
        return Ix.from(impl.parse(input))
                .map(new IxFunction<CssProperty, CssProperty>() {
                    @Override
                    public CssProperty apply(CssProperty cssProperty) {
                        return cssProperty.mutate();
                    }
                })
                .toList();
    }

    public interface Action<T> {
        void apply(@NonNull T t);
    }

    private static <T> void with(@NonNull T t, @NonNull Action<T> action) {
        action.apply(t);
    }

}