package io.noties.markwon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.noties.markwon.core.CorePlugin;

// @since 4.0.0
class RegistryImpl implements MarkwonPlugin.Registry {

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
                // core-plugin must always be the first one (if it's present)
                if (CorePlugin.class.isAssignableFrom(plugin.getClass())) {
                    plugins.add(0, plugin);
                } else {
                    plugins.add(plugin);
                }
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
