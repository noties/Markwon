package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.widget.TextView;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.image.AsyncDrawableLoader;

/**
 * @since 3.0.0
 */
public interface MarkwonPlugin {

    void configureParser(@NonNull Parser.Builder builder);

    void configureTheme(@NonNull MarkwonTheme.Builder builder);

    void configureImages(@NonNull AsyncDrawableLoader.Builder builder);

    void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder);

    void configureVisitor(@NonNull MarkwonVisitor.Builder builder);

    void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder);

    // can be used to configure own properties and use between plugins
    void configureRenderProps(@NonNull RenderProps renderProps);

    @NonNull
    String processMarkdown(@NonNull String markdown);

    void beforeRender(@NonNull Node node);

    void afterRender(@NonNull Node node, @NonNull MarkwonVisitor visitor);

    void beforeSetText(@NonNull TextView textView, @NonNull CharSequence markdown);

    // this method do not receive markdown like `beforeSetText` does because at this
    // point TextView already has markdown set and to manipulate spans one must
    // request them from TextView (getText())
    void afterSetText(@NonNull TextView textView);
}
