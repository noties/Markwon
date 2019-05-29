package ru.noties.markwon.sample.customextension;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.commonmark.parser.Parser;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonVisitor;

public class IconPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static IconPlugin create(@NonNull IconSpanProvider iconSpanProvider) {
        return new IconPlugin(iconSpanProvider);
    }

    private final IconSpanProvider iconSpanProvider;

    IconPlugin(@NonNull IconSpanProvider iconSpanProvider) {
        this.iconSpanProvider = iconSpanProvider;
    }

//    @NonNull
//    @Override
//    public Priority priority() {
//        // define images dependency
//        return Priority.after(ImagesPlugin.class);
//    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.customDelimiterProcessor(IconProcessor.create());
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(IconNode.class, new MarkwonVisitor.NodeVisitor<IconNode>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull IconNode iconNode) {

                final String name = iconNode.name();
                final String color = iconNode.color();
                final String size = iconNode.size();

                if (!TextUtils.isEmpty(name)
                        && !TextUtils.isEmpty(color)
                        && !TextUtils.isEmpty(size)) {

                    final int length = visitor.length();

                    visitor.builder().append(name);
                    visitor.setSpans(length, iconSpanProvider.provide(name, color, size));
                    visitor.builder().append(' ');
                }
            }
        });
    }

    @NonNull
    @Override
    public String processMarkdown(@NonNull String markdown) {
        return IconProcessor.prepare(markdown);
    }
}
