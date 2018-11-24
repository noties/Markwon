package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.widget.TextView;

import org.commonmark.parser.Parser;

import ru.noties.markwon.spans.MarkwonTheme;

public interface MarkwonPlugin {

    void configureParser(@NonNull Parser.Builder builder);

    void configureTheme(@NonNull MarkwonTheme.Builder builder);

    void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder);

    void configureVisitor(@NonNull MarkwonVisitor.Builder builder);

    // images
    // html

    @NonNull
    String processMarkdown(@NonNull String markdown);

    void beforeSetText(@NonNull TextView textView, @NonNull CharSequence markdown);

    void afterSetText(@NonNull TextView textView, @NonNull CharSequence markdown);
}
