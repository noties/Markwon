package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.widget.TextView;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.List;

class MarkwonImpl extends Markwon2 {

    private final Parser parser;
    private final MarkwonVisitor visitor;
    private final List<MarkwonPlugin> plugins;

    MarkwonImpl(
            @NonNull Parser parser,
            @NonNull MarkwonVisitor visitor,
            @NonNull List<MarkwonPlugin> plugins) {
        this.parser = parser;
        this.visitor = visitor;
        this.plugins = plugins;
    }

    @NonNull
    @Override
    public Node parse(@NonNull String input) {

        for (MarkwonPlugin plugin : plugins) {
            input = plugin.processMarkdown(input);
        }

        return parser.parse(input);
    }

    @NonNull
    @Override
    public CharSequence render(@NonNull Node node) {
        node.accept(visitor);
        return visitor.builder().text();
    }

    @NonNull
    @Override
    public CharSequence toMarkdown(@NonNull String input) {
        return render(parse(input));
    }

    @Override
    public void setMarkdown(@NonNull TextView textView, @NonNull String markdown) {
        setParsedMarkdown(textView, toMarkdown(markdown));
    }

    @Override
    public void setParsedMarkdown(@NonNull TextView textView, @NonNull CharSequence markdown) {

        for (MarkwonPlugin plugin : plugins) {
            plugin.beforeSetText(textView, markdown);
        }

        textView.setText(markdown);

        for (MarkwonPlugin plugin : plugins) {
            plugin.afterSetText(textView);
        }
    }
}
