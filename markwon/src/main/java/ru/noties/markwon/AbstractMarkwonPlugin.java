package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.text.Spanned;
import android.widget.TextView;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.html.MarkwonHtmlRenderer;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.priority.Priority;

/**
 * Class that extends {@link MarkwonPlugin} with all methods implemented (empty body)
 * for easier plugin implementation. Only required methods can be overriden
 *
 * @see MarkwonPlugin
 * @since 3.0.0
 */
public abstract class AbstractMarkwonPlugin implements MarkwonPlugin {

    /**
     * @inheritDoc
     */
    @Override
    public void configureParser(@NonNull Parser.Builder builder) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void configureHtmlRenderer(@NonNull MarkwonHtmlRenderer.Builder builder) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void configureRenderProps(@NonNull RenderProps renderProps) {

    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public Priority priority() {
        // by default all come after CorePlugin
        return Priority.after(CorePlugin.class);
    }

    /**
     * @inheritDoc
     */
    @NonNull
    @Override
    public String processMarkdown(@NonNull String markdown) {
        return markdown;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void beforeRender(@NonNull Node node) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void afterRender(@NonNull Node node, @NonNull MarkwonVisitor visitor) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void afterSetText(@NonNull TextView textView) {

    }
}
