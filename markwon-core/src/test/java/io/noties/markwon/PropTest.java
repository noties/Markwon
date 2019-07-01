package io.noties.markwon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PropTest {

    // get
    // get with default
    // require
    // set
    // clear

    private RenderProps props;
    private Prop<Boolean> prop;

    @Before
    public void before() {
        props = mock(RenderProps.class);
        prop = new Prop<>("a prop");
    }

    @Test
    public void methods_redirected_get() {

        prop.get(props);

        verify(props, times(1)).get(eq(prop));
    }

    @Test
    public void methods_redirected_get_with_default() {

        prop.get(props, false);

        verify(props, times(1)).get(eq(prop), eq(false));
    }

    @Test
    public void methods_redirected_require() {
        // require is a bit different as `require` has no place in renderProps
        // instead a Prop will throw an exception if requested prop is not in props

        when(props.get(eq(prop))).thenReturn(true);

        prop.require(props);

        verify(props, times(1)).get(eq(prop));
    }

    @Test
    public void methods_redirected_set() {

        prop.set(props, true);

        verify(props, times(1)).set(eq(prop), eq(true));
    }

    @Test
    public void methods_redirected_clear() {

        prop.clear(props);

        verify(props, times(1)).clear(eq(prop));
    }

    @Test
    public void require() {

        try {
            prop.require(props);
            fail();
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void has_hashcode_and_equals() {
        try {
            Prop.class.getDeclaredMethod("hashCode");
            Prop.class.getDeclaredMethod("equals", Object.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}