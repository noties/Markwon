package io.noties.markwon.sample.customextension;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.commonmark.parser.Parser;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonVisitor;

public class IconPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static IconPlugin create(@NonNull IconSpanProvider iconSpanProvider) {
        return new IconPlugin(iconSpanProvider);
    }

    private final IconSpanProvider iconSpanProvider;

    IconPlugin(@NonNull IconSpanProvider iconSpanProvider) {
        this.iconSpanProvider = iconSpanProvider;
    }

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
