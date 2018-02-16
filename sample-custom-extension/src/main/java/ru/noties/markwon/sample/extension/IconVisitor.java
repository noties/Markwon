package ru.noties.markwon.sample.extension;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.TextView;

import org.commonmark.node.CustomNode;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.renderer.SpannableMarkdownVisitor;

@SuppressWarnings("WeakerAccess")
public class IconVisitor extends SpannableMarkdownVisitor {

    private final SpannableBuilder builder;

    private final IconSpanProvider iconSpanProvider;

    public IconVisitor(
            @NonNull SpannableConfiguration configuration,
            @NonNull SpannableBuilder builder,
            @NonNull IconSpanProvider iconSpanProvider
    ) {
        super(configuration, builder);
        this.builder = builder;
        this.iconSpanProvider = iconSpanProvider;
    }

    @Override
    public void visit(CustomNode customNode) {
        if (!visitIconNode(customNode)) {
            super.visit(customNode);
        }
    }

    private boolean visitIconNode(@NonNull CustomNode customNode) {

        if (customNode instanceof IconNode) {

            final IconNode node = (IconNode) customNode;

            final String name = node.name();
            final String color = node.color();
            final String size = node.size();

            if (!TextUtils.isEmpty(name)
                    && !TextUtils.isEmpty(color)
                    && !TextUtils.isEmpty(size)) {

                final int length = builder.length();

                builder.append(name);
                builder.setSpan(iconSpanProvider.provide(name, color, size), length);
                builder.append(' ');
                
                return true;
            }
        }

        return false;
    }
}
