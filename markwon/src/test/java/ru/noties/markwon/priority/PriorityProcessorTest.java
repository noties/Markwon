package ru.noties.markwon.priority;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonPlugin;
import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.image.ImagesPlugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PriorityProcessorTest {

    private PriorityProcessor processor;

    @Before
    public void before() {
        processor = PriorityProcessor.create();
    }

    @Test
    public void empty_list() {
        final List<MarkwonPlugin> plugins = Collections.emptyList();
        assertEquals(0, processor.process(plugins).size());
    }

    @Test
    public void simple_two_plugins() {

        final MarkwonPlugin first = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.none();
            }
        };

        final MarkwonPlugin second = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(first.getClass());
            }
        };

        final List<MarkwonPlugin> plugins = processor.process(Arrays.asList(second, first));

        assertEquals(2, plugins.size());
        assertEquals(first, plugins.get(0));
        assertEquals(second, plugins.get(1));
    }

    @Test
    public void plugin_after_self() {

        final MarkwonPlugin plugin = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(getClass());
            }
        };

        try {
            processor.process(Collections.singletonList(plugin));
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("defined self as a dependency"));
        }
    }

    @Test
    public void unsatisfied_dependency() {

        final MarkwonPlugin plugin = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(ImagesPlugin.class);
            }
        };

        try {
            processor.process(Collections.singletonList(plugin));
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Markwon unsatisfied dependency found"));
        }
    }

    @Test
    public void subclass_found() {
        // when a plugin comes after another, but _another_ was subclassed and placed in the list

        final MarkwonPlugin core = new CorePlugin(false) {
        };
        final MarkwonPlugin plugin = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(CorePlugin.class);
            }
        };

        final List<MarkwonPlugin> plugins = processor.process(Arrays.asList(plugin, core));
        assertEquals(2, plugins.size());
        assertEquals(core, plugins.get(0));
        assertEquals(plugin, plugins.get(1));
    }

    @Test
    public void three_plugins_sequential() {

        final MarkwonPlugin first = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.none();
            }
        };

        final MarkwonPlugin second = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(first.getClass());
            }
        };

        final MarkwonPlugin third = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(second.getClass());
            }
        };

        final List<MarkwonPlugin> plugins = processor.process(Arrays.asList(third, second, first));
        assertEquals(3, plugins.size());
        assertEquals(first, plugins.get(0));
        assertEquals(second, plugins.get(1));
        assertEquals(third, plugins.get(2));
    }

    @Test
    public void plugin_duplicate() {

        final MarkwonPlugin plugin = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.none();
            }
        };

        try {
            processor.process(Arrays.asList(plugin, plugin));
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Markwon duplicate plugin found"));
        }
    }

    @Test
    public void multiple_after_3() {

        final MarkwonPlugin a1 = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.none();
            }
        };

        final MarkwonPlugin b1 = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(a1.getClass());
            }
        };

        final MarkwonPlugin c1 = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(a1.getClass(), b1.getClass());
            }
        };

        final List<MarkwonPlugin> plugins = processor.process(Arrays.asList(c1, a1, b1));
        assertEquals(3, plugins.size());
        assertEquals(a1, plugins.get(0));
        assertEquals(b1, plugins.get(1));
        assertEquals(c1, plugins.get(2));
    }

    @Test
    public void multiple_after_4() {

        final MarkwonPlugin a1 = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.none();
            }
        };

        final MarkwonPlugin b1 = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(a1.getClass());
            }
        };

        final MarkwonPlugin c1 = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(a1.getClass(), b1.getClass());
            }
        };

        final MarkwonPlugin d1 = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.builder()
                        .after(a1.getClass())
                        .after(b1.getClass())
                        .after(c1.getClass())
                        .build();
            }
        };

        final List<MarkwonPlugin> plugins = processor.process(Arrays.asList(c1, d1, a1, b1));
        assertEquals(4, plugins.size());
        assertEquals(a1, plugins.get(0));
        assertEquals(b1, plugins.get(1));
        assertEquals(c1, plugins.get(2));
        assertEquals(d1, plugins.get(3));
    }

    @Test
    public void cycle() {

        final class Holder {
            Class<? extends MarkwonPlugin> type;
        }
        final Holder holder = new Holder();

        final MarkwonPlugin first = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(holder.type);
            }
        };

        final MarkwonPlugin second = new AbstractMarkwonPlugin() {

            {
                holder.type = getClass();
            }

            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(first.getClass());
            }
        };

        try {
            processor.process(Arrays.asList(second, first));
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("being referenced by own dependency (cycle)"));
        }
    }

    @Test
    public void bigger_cycle() {

        final class Plugin extends NamedPlugin {

            private Priority priority;

            private Plugin(@NonNull String name) {
                super(name);
            }

            private void set(@NonNull MarkwonPlugin plugin) {
                priority = Priority.after(plugin.getClass());
            }

            @NonNull
            @Override
            public Priority priority() {
                return priority;
            }
        }

        final Plugin a = new Plugin("a");

        final List<MarkwonPlugin> plugins = new ArrayList<>();
        plugins.add(a);
        plugins.add(new NamedPlugin("b", plugins.get(plugins.size() - 1)) {
        });
        plugins.add(new NamedPlugin("c", plugins.get(plugins.size() - 1)) {
        });
        plugins.add(new NamedPlugin("d", plugins.get(plugins.size() - 1)) {
        });
        plugins.add(new NamedPlugin("e", plugins.get(plugins.size() - 1)) {
        });
        plugins.add(new NamedPlugin("f", plugins.get(plugins.size() - 1)) {
        });
        plugins.add(new NamedPlugin("g", plugins.get(plugins.size() - 1)) {
        });

        // link with the last one
        a.set(plugins.get(plugins.size() - 1));

        try {
            processor.process(plugins);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("being referenced by own dependency (cycle)"));
        }
    }

    @Test
    public void deep_tree() {

        // we must create subclasses in order to register them like this (otherwise -> duplicates)
        final MarkwonPlugin a = new NamedPlugin("a") {
        };
        final MarkwonPlugin b1 = new NamedPlugin("b1", a) {
        };
        final MarkwonPlugin b2 = new NamedPlugin("b2", a) {
        };
        final MarkwonPlugin c1 = new NamedPlugin("c1", b1) {
        };
        final MarkwonPlugin c2 = new NamedPlugin("c2", b1) {
        };
        final MarkwonPlugin c3 = new NamedPlugin("c3", b2) {
        };
        final MarkwonPlugin c4 = new NamedPlugin("c4", b2) {
        };
        final MarkwonPlugin d1 = new NamedPlugin("d1", c1) {
        };
        final MarkwonPlugin e1 = new NamedPlugin("e1", d1, c2, c3, c4) {
        };

        final List<MarkwonPlugin> plugins = processor.process(Arrays.asList(b2, b1,
                a, e1, c4, c3, c2, c1, d1));

        // a is first
        // b1 + b2 -> second+third
        // c1 + c2 + c3 + c4 -> forth, fifth, sixth, seventh
        // d1 -> 8th
        // e1 -> 9th

        assertEquals(9, plugins.size());
        assertEquals(a, plugins.get(0));
        assertEquals(new HashSet<>(Arrays.asList(b1, b2)), new HashSet<>(plugins.subList(1, 3)));
        assertEquals(new HashSet<>(Arrays.asList(c1, c2, c3, c4)), new HashSet<>(plugins.subList(3, 7)));
        assertEquals(d1, plugins.get(7));
        assertEquals(e1, plugins.get(8));
    }

    @Test
    public void multiple_detached() {

        // when graph has independent elements that are not connected with each other
        final MarkwonPlugin a0 = new NamedPlugin("a0") {
        };
        final MarkwonPlugin a1 = new NamedPlugin("a1", a0) {
        };
        final MarkwonPlugin a2 = new NamedPlugin("a2", a1) {
        };

        final MarkwonPlugin b0 = new NamedPlugin("b0") {
        };
        final MarkwonPlugin b1 = new NamedPlugin("b1", b0) {
        };
        final MarkwonPlugin b2 = new NamedPlugin("b2", b1) {
        };

        final List<MarkwonPlugin> plugins = processor.process(Arrays.asList(
                b2, a2, a0, b0, b1, a1));

        assertEquals(6, plugins.size());

        assertEquals(new HashSet<>(Arrays.asList(a0, b0)), new HashSet<>(plugins.subList(0, 2)));
        assertEquals(new HashSet<>(Arrays.asList(a1, b1)), new HashSet<>(plugins.subList(2, 4)));
        assertEquals(new HashSet<>(Arrays.asList(a2, b2)), new HashSet<>(plugins.subList(4, 6)));
    }

    private static abstract class NamedPlugin extends AbstractMarkwonPlugin {

        private final String name;
        private final Priority priority;

        NamedPlugin(@NonNull String name) {
            this(name, (Priority) null);
        }

        NamedPlugin(@NonNull String name, @Nullable MarkwonPlugin plugin) {
            this(name, plugin != null ? Priority.after(plugin.getClass()) : null);
        }

        NamedPlugin(@NonNull String name, MarkwonPlugin... plugins) {
            this(name, of(plugins));
        }

        NamedPlugin(@NonNull String name, @Nullable Class<? extends MarkwonPlugin> plugin) {
            this(name, plugin != null ? Priority.after(plugin) : null);
        }

        NamedPlugin(@NonNull String name, @Nullable Priority priority) {
            this.name = name;
            this.priority = priority;
        }

        @NonNull
        @Override
        public Priority priority() {
            return priority != null
                    ? priority
                    : Priority.none();
        }

        @Override
        public String toString() {
            return "NamedPlugin{" +
                    "name='" + name + '\'' +
                    '}';
        }

        @NonNull
        private static Priority of(@NonNull MarkwonPlugin... plugins) {
            if (plugins.length == 0) return Priority.none();
            final Priority.Builder builder = Priority.builder();
            for (MarkwonPlugin plugin : plugins) {
                builder.after(plugin.getClass());
            }
            return builder.build();
        }
    }
}