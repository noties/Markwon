package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.widget.TextView;

import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ru.noties.markwon.core.MarkwonTheme;

/**
 * @since 3.0.0
 */
class MarkwonBuilderImpl implements Markwon.Builder {

    private final Context context;

    private final List<MarkwonPlugin> plugins = new ArrayList<>(3);

    private TextView.BufferType bufferType = TextView.BufferType.SPANNABLE;

    MarkwonBuilderImpl(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Markwon.Builder bufferType(@NonNull TextView.BufferType bufferType) {
        this.bufferType = bufferType;
        return this;
    }

    @NonNull
    @Override
    public Markwon.Builder usePlugin(@NonNull MarkwonPlugin plugin) {
        plugins.add(plugin);
        return this;
    }

    @NonNull
    @Override
    public Markwon.Builder usePlugins(@NonNull Iterable<? extends MarkwonPlugin> plugins) {

        final Iterator<? extends MarkwonPlugin> iterator = plugins.iterator();

        MarkwonPlugin plugin;

        while (iterator.hasNext()) {
            plugin = iterator.next();
            if (plugin == null) {
                throw new NullPointerException();
            }
            this.plugins.add(plugin);
        }

        return this;
    }

    @NonNull
    @Override
    public Markwon build() {

        if (plugins.isEmpty()) {
            throw new IllegalStateException("No plugins were added to this builder. Use #usePlugin " +
                    "method to add them");
        }

        // please note that this method must not modify supplied collection
        // if nothing should be done -> the same collection can be returned
        final List<MarkwonPlugin> plugins = preparePlugins(this.plugins);

        final Parser.Builder parserBuilder = new Parser.Builder();
        final MarkwonTheme.Builder themeBuilder = MarkwonTheme.builderWithDefaults(context);
        final MarkwonConfiguration.Builder configurationBuilder = new MarkwonConfiguration.Builder();
        final MarkwonVisitor.Builder visitorBuilder = new MarkwonVisitorImpl.BuilderImpl();
        final MarkwonSpansFactory.Builder spanFactoryBuilder = new MarkwonSpansFactoryImpl.BuilderImpl();

        for (MarkwonPlugin plugin : plugins) {
            plugin.configureParser(parserBuilder);
            plugin.configureTheme(themeBuilder);
            plugin.configureConfiguration(configurationBuilder);
            plugin.configureVisitor(visitorBuilder);
            plugin.configureSpansFactory(spanFactoryBuilder);
        }

        final MarkwonConfiguration configuration = configurationBuilder.build(
                themeBuilder.build(),
                spanFactoryBuilder.build());

        final RenderProps renderProps = new RenderPropsImpl();

        return new MarkwonImpl(
                bufferType,
                parserBuilder.build(),
                visitorBuilder.build(configuration, renderProps),
                Collections.unmodifiableList(plugins)
        );
    }

    @VisibleForTesting
    @NonNull
    static List<MarkwonPlugin> preparePlugins(@NonNull List<MarkwonPlugin> plugins) {
        return new RegistryImpl(plugins).process();
    }

    // @since 4.0.0-SNAPSHOT
    private static class RegistryImpl implements MarkwonPlugin.Registry {

        private final List<MarkwonPlugin> origin;
        private final List<MarkwonPlugin> plugins;
        private final Set<MarkwonPlugin> pending;

        RegistryImpl(@NonNull List<MarkwonPlugin> origin) {
            this.origin = origin;
            this.plugins = new ArrayList<>(origin.size());
            this.pending = new HashSet<>(3);
        }

        @NonNull
        @Override
        public <P extends MarkwonPlugin> P require(@NonNull Class<P> plugin) {
            return get(plugin);
        }

        @Override
        public <P extends MarkwonPlugin> void require(
                @NonNull Class<P> plugin,
                @NonNull MarkwonPlugin.Action<? super P> action) {
            action.apply(get(plugin));
        }

        @NonNull
        List<MarkwonPlugin> process() {
            for (MarkwonPlugin plugin : origin) {
                configure(plugin);
            }
            return plugins;
        }

        private void configure(@NonNull MarkwonPlugin plugin) {

            // important -> check if it's in plugins
            //  if it is -> no need to configure (already configured)

            if (!plugins.contains(plugin)) {

                if (pending.contains(plugin)) {
                    throw new IllegalStateException("Cyclic dependency chain found: " + pending);
                }

                // start tracking plugins that are pending for configuration
                pending.add(plugin);

                plugin.configure(this);

                // stop pending tracking
                pending.remove(plugin);

                // check again if it's included (a child might've configured it already)
                // add to out-collection if not already present
                // this is a bit different from `find` method as it does check for exact instance
                // and not a sub-type
                if (!plugins.contains(plugin)) {
                    plugins.add(plugin);
                }
            }
        }

        @NonNull
        private <P extends MarkwonPlugin> P get(@NonNull Class<P> type) {

            // check if present already in plugins
            // find in origin, if not found -> throw, else add to out-plugins

            P plugin = find(plugins, type);

            if (plugin == null) {

                plugin = find(origin, type);

                if (plugin == null) {
                    throw new IllegalStateException("Requested plugin is not added: " +
                            "" + type.getName() + ", plugins: " + origin);
                }

                configure(plugin);
            }

            return plugin;
        }

        @Nullable
        private static <P extends MarkwonPlugin> P find(
                @NonNull List<MarkwonPlugin> plugins,
                @NonNull Class<P> type) {
            for (MarkwonPlugin plugin : plugins) {
                if (type.isAssignableFrom(plugin.getClass())) {
                    //noinspection unchecked
                    return (P) plugin;
                }
            }
            return null;
        }
    }
}
