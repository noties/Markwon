package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.widget.TextView;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.image.AsyncDrawableLoader;

public abstract class AbstractMarkwonPlugin implements MarkwonPlugin {
    @Override
    public void configureParser(@NonNull Parser.Builder builder) {

    }

    @Override
    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {

    }

    @Override
    public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {

    }

    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {

    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {

    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {

    }

    @Override
    public void configureRenderProps(@NonNull RenderProps renderProps) {

    }

    @NonNull
    @Override
    public String processMarkdown(@NonNull String markdown) {
        return markdown;
    }

    @Override
    public void beforeRender(@NonNull Node node) {

    }

    @Override
    public void afterRender(@NonNull Node node, @NonNull MarkwonVisitor visitor) {

    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull CharSequence markdown) {

    }

    @Override
    public void afterSetText(@NonNull TextView textView) {

    }
}
