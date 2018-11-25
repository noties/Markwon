package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;

import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.spans.MarkwonTheme;

class MarkwonBuilderImpl implements Markwon2.Builder {

    private final Context context;

    private final List<MarkwonPlugin> plugins = new ArrayList<>(3);

    MarkwonBuilderImpl(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Markwon2.Builder use(@NonNull MarkwonPlugin plugin) {
        plugins.add(plugin);
        return this;
    }

    @NonNull
    @Override
    public Markwon2 build() {

        final Parser.Builder parserBuilder = new Parser.Builder();
        final MarkwonTheme.Builder themeBuilder = MarkwonTheme.builderWithDefaults(context);
        final AsyncDrawableLoader.Builder asyncDrawableLoaderBuilder = new AsyncDrawableLoader.Builder();
        final MarkwonConfiguration.Builder configurationBuilder = new MarkwonConfiguration.Builder(context);
        final MarkwonVisitor.Builder visitorBuilder = new MarkwonVisitorImpl.BuilderImpl();

        for (MarkwonPlugin plugin : plugins) {
            plugin.configureParser(parserBuilder);
            plugin.configureTheme(themeBuilder);
            plugin.configureImages(asyncDrawableLoaderBuilder);
            plugin.configureConfiguration(configurationBuilder);
            plugin.configureVisitor(visitorBuilder);
        }

        final MarkwonConfiguration configuration = configurationBuilder.build(
                themeBuilder.build(),
                asyncDrawableLoaderBuilder.build());

        return new MarkwonImpl(
                parserBuilder.build(),
                visitorBuilder.build(configuration),
                Collections.unmodifiableList(plugins)
        );
    }
}
