package io.noties.markwon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RenderPropsImplTest {

    private RenderPropsImpl props;
    private Prop<String> prop;

    @Before
    public void before() {
        props = new RenderPropsImpl();
        prop = Prop.of("a prop of byte");
    }

    @Test
    public void get() {

        // initial
        assertNull(props.get(prop));

        // update value
        props.set(prop, "get-value");

        assertEquals("get-value", props.get(prop));
    }

    @Test
    public void get_with_default() {

        // validate that it's null
        assertNull(props.get(prop));

        assertEquals("a-default", props.get(prop, "a-default"));

        // update value (so, no default will be returned)
        props.set(prop, "get-with-default-value");

        assertEquals("get-with-default-value", props.get(prop, "not-used"));
    }

    @Test
    public void set() {

        assertNull(props.get(prop));

        props.set(prop, "set-value");
        assertEquals("set-value", props.get(prop));

        // update (aka delete) with null value
        props.set(prop, null);
        assertNull(props.get(prop));

        // multiple set's (last one will be used, each one replaces previous)
        props.set(prop, "value-1");
        props.set(prop, "value-2");
        props.set(prop, "value-3");

        assertEquals("value-3", props.get(prop));
    }

    @Test
    public void clear() {

        props.set(prop, "clear-value");

        assertEquals("clear-value", props.get(prop));

        props.clear(prop);

        assertNull(props.get(prop));
    }

    @Test
    public void clear_all() {

        final List<Prop<String>> list = Arrays.asList(
                Prop.<String>of("#1"),
                Prop.<String>of("#2"),
                Prop.<String>of("#3"),
                Prop.<String>of("#4"),
                Prop.<String>of("#5"));

        // validate that all nulls
        for (Prop<String> prop : list) {
            assertNull(props.get(prop));
        }

        // set each
        for (Prop<String> prop : list) {
            props.set(prop, prop.name());
        }

        // validate that all are not-null
        for (Prop<String> prop : list) {
            assertNotNull(props.get(prop));
        }

        props.clearAll();

        // validate that all are nulls
        for (Prop<String> prop : list) {
            assertNull(props.get(prop));
        }
    }
}