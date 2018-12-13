package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.core.MarkwonTheme;

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
    public Markwon.Builder use(@NonNull MarkwonPlugin plugin) {
        plugins.add(plugin);
        return this;
    }

    @NonNull
    @Override
    public Markwon build() {

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
                bufferType,
                parserBuilder.build(),
                visitorBuilder.build(configuration),
                Collections.unmodifiableList(plugins)
        );
    }
}
