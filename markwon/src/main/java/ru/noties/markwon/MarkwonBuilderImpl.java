package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.image.AsyncDrawableLoader;

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
}
