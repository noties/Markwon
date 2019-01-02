package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.widget.TextView;

import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.priority.PriorityProcessor;

/**
 * @since 3.0.0
 */
@SuppressWarnings("WeakerAccess")
class MarkwonBuilderImpl implements Markwon.Builder {

    private final Context context;

    private final List<MarkwonPlugin> plugins = new ArrayList<>(3);

    private TextView.BufferType bufferType = TextView.BufferType.SPANNABLE;

    private PriorityProcessor priorityProcessor;

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

    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public MarkwonBuilderImpl priorityProcessor(@NonNull PriorityProcessor priorityProcessor) {
        this.priorityProcessor = priorityProcessor;
        return this;
    }

    @NonNull
    @Override
    public Markwon build() {

        if (plugins.isEmpty()) {
            throw new IllegalStateException("No plugins were added to this builder. Use #usePlugin " +
                    "method to add them");
        }

        // this class will sort plugins to match a priority/dependency graph that we have
        PriorityProcessor priorityProcessor = this.priorityProcessor;
        if (priorityProcessor == null) {
            // strictly speaking we do not need updating this field
            // as we are not building this class to be reused between multiple `build` calls
            priorityProcessor = this.priorityProcessor = PriorityProcessor.create();
        }

        // please note that this method must not modify supplied collection
        // if nothing should be done -> the same collection can be returned
        final List<MarkwonPlugin> plugins = preparePlugins(priorityProcessor, this.plugins);

        final Parser.Builder parserBuilder = new Parser.Builder();
        final MarkwonTheme.Builder themeBuilder = MarkwonTheme.builderWithDefaults(context);
        final AsyncDrawableLoader.Builder asyncDrawableLoaderBuilder = new AsyncDrawableLoader.Builder();
        final MarkwonConfiguration.Builder configurationBuilder = new MarkwonConfiguration.Builder();
        final MarkwonVisitor.Builder visitorBuilder = new MarkwonVisitorImpl.BuilderImpl();
        final MarkwonSpansFactory.Builder spanFactoryBuilder = new MarkwonSpansFactoryImpl.BuilderImpl();
        final RenderProps renderProps = new RenderPropsImpl();

        for (MarkwonPlugin plugin : plugins) {
            plugin.configureParser(parserBuilder);
            plugin.configureTheme(themeBuilder);
            plugin.configureImages(asyncDrawableLoaderBuilder);
            plugin.configureConfiguration(configurationBuilder);
            plugin.configureVisitor(visitorBuilder);
            plugin.configureSpansFactory(spanFactoryBuilder);
        }

        final MarkwonConfiguration configuration = configurationBuilder.build(
                themeBuilder.build(),
                asyncDrawableLoaderBuilder.build(),
                spanFactoryBuilder.build());

        return new MarkwonImpl(
                bufferType,
                parserBuilder.build(),
                visitorBuilder.build(configuration, renderProps),
                Collections.unmodifiableList(plugins)
        );
    }

    @VisibleForTesting
    @NonNull
    static List<MarkwonPlugin> preparePlugins(
            @NonNull PriorityProcessor priorityProcessor,
            @NonNull List<MarkwonPlugin> plugins) {

        // with this method we will ensure that CorePlugin is added IF and ONLY IF
        // there are plugins that depend on it. If CorePlugin is added, or there are
        // no plugins that require it, CorePlugin won't be added
        final List<MarkwonPlugin> out = ensureImplicitCoreIfHasDependents(plugins);

        return priorityProcessor.process(out);
    }

    // this method will _implicitly_ add CorePlugin if there is at least one plugin
    // that depends on CorePlugin
    @VisibleForTesting
    @NonNull
    static List<MarkwonPlugin> ensureImplicitCoreIfHasDependents(@NonNull List<MarkwonPlugin> plugins) {
        // loop over plugins -> if CorePlugin is found -> break;
        // iterate over all plugins and check if CorePlugin is requested

        boolean hasCore = false;
        boolean hasCoreDependents = false;

        for (MarkwonPlugin plugin : plugins) {

            // here we do not check for exact match (a user could've subclasses CorePlugin
            // and supplied it. In this case we DO NOT implicitly add CorePlugin
            if (CorePlugin.class.isAssignableFrom(plugin.getClass())) {
                hasCore = true;
                break;
            }

            // if plugin has CorePlugin in dependencies -> mark for addition
            if (!hasCoreDependents) {
                // here we check for direct CorePlugin, if it's not CorePlugin (exact, not a subclass
                // or something -> ignore)
                if (plugin.priority().after().contains(CorePlugin.class)) {
                    hasCoreDependents = true;
                }
            }
        }

        // important thing here is to check if corePlugin is added
        // add it _only_ if it's not present
        if (hasCoreDependents && !hasCore) {
            final List<MarkwonPlugin> out = new ArrayList<>(plugins.size() + 1);
            // add default instance of CorePlugin
            out.add(CorePlugin.create());
            out.addAll(plugins);
            return out;
        }

        return plugins;
    }
}
