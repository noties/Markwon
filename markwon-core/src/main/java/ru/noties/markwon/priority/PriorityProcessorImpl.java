package ru.noties.markwon.priority;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.noties.markwon.MarkwonPlugin;

import static java.lang.Math.max;

class PriorityProcessorImpl extends PriorityProcessor {

    @NonNull
    @Override
    public List<MarkwonPlugin> process(@NonNull List<MarkwonPlugin> in) {

        // create new collection based on supplied argument
        final List<MarkwonPlugin> plugins = new ArrayList<>(in);

        final int size = plugins.size();

        final Map<Class<? extends MarkwonPlugin>, Set<Class<? extends MarkwonPlugin>>> map =
                new HashMap<>(size);

        for (MarkwonPlugin plugin : plugins) {
            if (map.put(plugin.getClass(), new HashSet<>(plugin.priority().after())) != null) {
                throw new IllegalStateException(String.format("Markwon duplicate plugin " +
                        "found `%s`: %s", plugin.getClass().getName(), plugin));
            }
        }

        final Map<MarkwonPlugin, Integer> cache = new HashMap<>(size);
        for (MarkwonPlugin plugin : plugins) {
            cache.put(plugin, eval(plugin, map));
        }

        Collections.sort(plugins, new PriorityComparator(cache));

        return plugins;
    }

    private static int eval(
            @NonNull MarkwonPlugin plugin,
            @NonNull Map<Class<? extends MarkwonPlugin>, Set<Class<? extends MarkwonPlugin>>> map) {

        final Set<Class<? extends MarkwonPlugin>> set = map.get(plugin.getClass());

        // no dependencies
        if (set.isEmpty()) {
            return 0;
        }

        final Class<? extends MarkwonPlugin> who = plugin.getClass();

        int max = 0;

        for (Class<? extends MarkwonPlugin> dependency : set) {
            max = max(max, eval(who, dependency, map));
        }

        return 1 + max;
    }

    // we need to count the number of steps to a root node (which has no parents)
    private static int eval(
            @NonNull Class<? extends MarkwonPlugin> who,
            @NonNull Class<? extends MarkwonPlugin> plugin,
            @NonNull Map<Class<? extends MarkwonPlugin>, Set<Class<? extends MarkwonPlugin>>> map) {

        // exact match
        Set<Class<? extends MarkwonPlugin>> set = map.get(plugin);

        if (set == null) {

            // let's try to find inexact type (overridden/subclassed)
            for (Map.Entry<Class<? extends MarkwonPlugin>, Set<Class<? extends MarkwonPlugin>>> entry : map.entrySet()) {
                if (plugin.isAssignableFrom(entry.getKey())) {
                    set = entry.getValue();
                    break;
                }
            }

            if (set == null) {
                // unsatisfied dependency
                throw new IllegalStateException(String.format("Markwon unsatisfied dependency found. " +
                                "Plugin `%s` comes after `%s` but it is not added.",
                        who.getName(), plugin.getName()));
            }
        }

        if (set.isEmpty()) {
            return 0;
        }

        int value = 1;

        for (Class<? extends MarkwonPlugin> dependency : set) {

            // a case when a plugin defines `Priority.after(getClass)` or being
            // referenced by own dependency (even indirect)
            if (who.equals(dependency)) {
                throw new IllegalStateException(String.format("Markwon plugin `%s` defined self " +
                        "as a dependency or being referenced by own dependency (cycle)", who.getName()));
            }

            value += eval(who, dependency, map);
        }

        return value;
    }

    private static class PriorityComparator implements Comparator<MarkwonPlugin> {

        private final Map<MarkwonPlugin, Integer> map;

        PriorityComparator(@NonNull Map<MarkwonPlugin, Integer> map) {
            this.map = map;
        }

        @Override
        public int compare(MarkwonPlugin o1, MarkwonPlugin o2) {
            return map.get(o1).compareTo(map.get(o2));
        }
    }
}
