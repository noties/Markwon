package io.noties.markwon;

import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import io.noties.markwon.core.MarkwonTheme;

/**
 * Class that extends {@link MarkwonPlugin} with all methods implemented (empty body)
 * for easier plugin implementation. Only required methods can be overriden
 *
 * @see MarkwonPlugin
 * @since 3.0.0
 */
public abstract class AbstractMarkwonPlugin implements MarkwonPlugin {

    @Override
    public void configure(@NonNull Registry registry) {

    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {

    }

    @Override
    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {

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
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {

    }

    @Override
    public void afterSetText(@NonNull TextView textView) {

    }
}
