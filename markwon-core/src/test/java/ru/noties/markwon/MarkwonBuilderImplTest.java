package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.text.Spanned;
import android.widget.TextView;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.html.MarkwonHtmlRenderer;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.priority.Priority;
import ru.noties.markwon.priority.PriorityProcessor;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.noties.markwon.MarkwonBuilderImpl.ensureImplicitCoreIfHasDependents;
import static ru.noties.markwon.MarkwonBuilderImpl.preparePlugins;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MarkwonBuilderImplTest {

    @Test
    public void implicit_core_created() {
        // a plugin explicitly requests CorePlugin, but CorePlugin is not added manually by user
        // we validate that default CorePlugin instance is added

        final MarkwonPlugin plugin = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                // strictly speaking we do not need to override this method
                // as all children of AbstractMarkwonPlugin specify CorePlugin as a dependency.
                // but we still add it to make things explicit and future proof, if this
                // behavior changes
                return Priority.after(CorePlugin.class);
            }
        };

        final List<MarkwonPlugin> plugins = ensureImplicitCoreIfHasDependents(Collections.singletonList(plugin));

        assertThat(plugins, hasSize(2));
        assertThat(plugins, hasItem(isA(CorePlugin.class)));
    }

    @Test
    public void implicit_core_no_dependents_not_added() {
        final MarkwonPlugin a = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.none();
            }
        };

        final MarkwonPlugin b = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(a.getClass());
            }
        };

        final List<MarkwonPlugin> plugins = ensureImplicitCoreIfHasDependents(Arrays.asList(a, b));
        assertThat(plugins, hasSize(2));
        assertThat(plugins, not(hasItem(isA(CorePlugin.class))));
    }

    @Test
    public void implicit_core_present() {
        // if core is present it won't be added (whether or not there are dependents)

        final MarkwonPlugin plugin = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(CorePlugin.class);
            }
        };

        final CorePlugin corePlugin = CorePlugin.create();

        final List<MarkwonPlugin> plugins = ensureImplicitCoreIfHasDependents(Arrays.asList(plugin, corePlugin));
        assertThat(plugins, hasSize(2));
        assertThat(plugins, hasItem(plugin));
        assertThat(plugins, hasItem(corePlugin));
    }

    @Test
    public void implicit_core_subclass_present() {
        // core was subclassed by a user and provided (implicit core won't be added)

        final MarkwonPlugin plugin = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(CorePlugin.class);
            }
        };

        // our subclass
        final CorePlugin corePlugin = new CorePlugin() {

        };

        final List<MarkwonPlugin> plugins = ensureImplicitCoreIfHasDependents(Arrays.asList(plugin, corePlugin));
        assertThat(plugins, hasSize(2));
        assertThat(plugins, hasItem(plugin));
        assertThat(plugins, hasItem(corePlugin));
    }

    @Test
    public void prepare_plugins() {
        // validate that prepare plugins is calling `ensureImplicitCoreIfHasDependents` and
        // priority processor

        final PriorityProcessor priorityProcessor = mock(PriorityProcessor.class);
        when(priorityProcessor.process(ArgumentMatchers.<MarkwonPlugin>anyList()))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        return invocation.getArgument(0);
                    }
                });

        final MarkwonPlugin plugin = new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(CorePlugin.class);
            }
        };

        final List<MarkwonPlugin> plugins = preparePlugins(priorityProcessor, Collections.singletonList(plugin));
        assertThat(plugins, hasSize(2));
        assertThat(plugins, hasItem(plugin));
        assertThat(plugins, hasItem(isA(CorePlugin.class)));

        verify(priorityProcessor, times(1))
                .process(ArgumentMatchers.<MarkwonPlugin>anyList());
    }

    @Test
    public void user_supplied_priority_processor() {
        // verify that if user supplied priority processor it will be used

        final PriorityProcessor priorityProcessor = mock(PriorityProcessor.class);
        final MarkwonBuilderImpl impl = new MarkwonBuilderImpl(RuntimeEnvironment.application);

        // add some plugin (we do not care which one, but it must be present as we do not
        // allow empty plugins list)
        impl.usePlugin(new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.none();
            }
        });
        impl.priorityProcessor(priorityProcessor);
        impl.build();

        verify(priorityProcessor, times(1)).process(ArgumentMatchers.<MarkwonPlugin>anyList());
    }

    @Test
    public void no_plugins_added_throws() {
        // there is no sense in having an instance with no plugins registered

        try {
            new MarkwonBuilderImpl(RuntimeEnvironment.application).build();
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), e.getMessage(), containsString("No plugins were added"));
        }
    }

    @Test
    public void plugin_configured() {
        // verify that all configuration methods (applicable) are called

        final MarkwonPlugin plugin = mock(MarkwonPlugin.class);
        when(plugin.priority()).thenReturn(Priority.none());

        final MarkwonBuilderImpl impl = new MarkwonBuilderImpl(RuntimeEnvironment.application);
        impl.usePlugin(plugin).build();

        verify(plugin, times(1)).configureParser(any(Parser.Builder.class));
        verify(plugin, times(1)).configureTheme(any(MarkwonTheme.Builder.class));
        verify(plugin, times(1)).configureImages(any(AsyncDrawableLoader.Builder.class));
        verify(plugin, times(1)).configureConfiguration(any(MarkwonConfiguration.Builder.class));
        verify(plugin, times(1)).configureVisitor(any(MarkwonVisitor.Builder.class));
        verify(plugin, times(1)).configureSpansFactory(any(MarkwonSpansFactory.Builder.class));
        verify(plugin, times(1)).configureHtmlRenderer(any(MarkwonHtmlRenderer.Builder.class));

        // we do not know how many times exactly, but at least once it must be called
        verify(plugin, atLeast(1)).priority();

        // note, no render props -> they must be configured on render stage
        verify(plugin, times(0)).processMarkdown(anyString());
        verify(plugin, times(0)).beforeRender(any(Node.class));
        verify(plugin, times(0)).afterRender(any(Node.class), any(MarkwonVisitor.class));
        verify(plugin, times(0)).beforeSetText(any(TextView.class), any(Spanned.class));
        verify(plugin, times(0)).afterSetText(any(TextView.class));
    }
}