package io.noties.markwon;

import androidx.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.noties.markwon.core.CorePlugin;

import static io.noties.markwon.MarkwonAssert.assertMessageContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RegistryImplTest {

    @Test
    public void single_plugin_requires_self() {
        // detect recursive require

        final class Plugin extends AbstractMarkwonPlugin {
            @Override
            public void configure(@NonNull Registry registry) {
                registry.require(Plugin.class);
            }
        }
        final MarkwonPlugin plugin = new Plugin();

        final RegistryImpl impl = new RegistryImpl(Collections.singletonList(plugin));

        try {
            impl.process();
        } catch (Throwable t) {
            assertMessageContains(t, "Cyclic dependency chain found");
        }
    }

    @Test
    public void plugins_dependency_cycle() {

        final Map<String, Class<? extends MarkwonPlugin>> map = new HashMap<>();

        final class A extends AbstractMarkwonPlugin {
            @Override
            public void configure(@NonNull Registry registry) {
                //noinspection ConstantConditions
                registry.require(map.get("A"));
            }
        }

        final class B extends AbstractMarkwonPlugin {
            @Override
            public void configure(@NonNull Registry registry) {
                //noinspection ConstantConditions
                registry.require(map.get("B"));
            }
        }

        final class C extends AbstractMarkwonPlugin {
            @Override
            public void configure(@NonNull Registry registry) {
                //noinspection ConstantConditions
                registry.require(map.get("C"));
            }
        }

        map.put("A", B.class);
        map.put("B", C.class);
        map.put("C", A.class);

        final RegistryImpl impl =
                new RegistryImpl(Arrays.asList((MarkwonPlugin) new A(), new B(), new C()));

        try {
            impl.process();
            fail();
        } catch (Throwable t) {
            assertMessageContains(t, "Cyclic dependency chain found");
        }
    }

    @Test
    public void plugins_no_dependency_cycle() {

        final class C extends AbstractMarkwonPlugin {
        }

        final class B extends AbstractMarkwonPlugin {
            @Override
            public void configure(@NonNull Registry registry) {
                registry.require(C.class);
            }
        }

        final class A extends AbstractMarkwonPlugin {
            @Override
            public void configure(@NonNull Registry registry) {
                registry.require(B.class);
            }
        }

        final RegistryImpl impl =
                new RegistryImpl(Arrays.asList((MarkwonPlugin) new A(), new B(), new C()));

        impl.process();
    }

    @Test
    public void dependency_not_satisfied() {
        // when require is called for plugin not added

        final class A extends AbstractMarkwonPlugin {
        }

        final class B extends AbstractMarkwonPlugin {
            @Override
            public void configure(@NonNull Registry registry) {
                registry.require(A.class);
            }
        }

        final RegistryImpl impl =
                new RegistryImpl(Collections.singletonList((MarkwonPlugin) new B()));

        try {
            impl.process();
            fail();
        } catch (Throwable t) {
            assertMessageContains(t, "Requested plugin is not added");
            assertMessageContains(t, A.class.getName()); // ? if it's null for local class?
        }
    }

    @Test
    public void core_plugin_first() {
        // if core-plugin is present, hen it should be the first one

        final CorePlugin plugin = CorePlugin.create();

        final RegistryImpl impl = new RegistryImpl(Arrays.asList(
                mock(MarkwonPlugin.class),
                mock(MarkwonPlugin.class),
                plugin
        ));

        final List<MarkwonPlugin> plugins = impl.process();
        assertEquals(3, plugins.size());
        assertEquals(plugin, plugins.get(0));
    }

    @Test
    public void correct_order() {

        final class A extends AbstractMarkwonPlugin {
        }

        final class B extends AbstractMarkwonPlugin {
            @Override
            public void configure(@NonNull Registry registry) {
                registry.require(A.class);
            }
        }

        final class C extends AbstractMarkwonPlugin {
            @Override
            public void configure(@NonNull Registry registry) {
                registry.require(B.class);
            }
        }

        final A a = new A();
        final B b = new B();
        final C c = new C();

        final RegistryImpl impl = new RegistryImpl(Arrays.asList(
                (MarkwonPlugin) c, b, a));

        final List<MarkwonPlugin> plugins = impl.process();
        assertEquals(3, plugins.size());
        assertEquals(a, plugins.get(0));
        assertEquals(b, plugins.get(1));
        assertEquals(c, plugins.get(2));
    }
}