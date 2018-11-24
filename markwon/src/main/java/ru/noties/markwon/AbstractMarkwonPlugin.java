package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.widget.TextView;

import org.commonmark.parser.Parser;

import ru.noties.markwon.spans.MarkwonTheme;

public abstract class AbstractMarkwonPlugin implements MarkwonPlugin {
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

    @NonNull
    @Override
    public String processMarkdown(@NonNull String markdown) {
        return markdown;
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull CharSequence markdown) {

    }

    @Override
    public void afterSetText(@NonNull TextView textView, @NonNull CharSequence markdown) {

    }
}
