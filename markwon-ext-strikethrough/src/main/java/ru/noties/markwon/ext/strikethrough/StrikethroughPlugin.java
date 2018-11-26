package ru.noties.markwon.ext.strikethrough;

import android.support.annotation.NonNull;
import android.text.style.StrikethroughSpan;

import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.parser.Parser;

import java.util.Collections;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonVisitor;

public class StrikethroughPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static StrikethroughPlugin create() {
        return new StrikethroughPlugin();
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.extensions(Collections.singleton(StrikethroughExtension.create()));
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Strikethrough.class, new MarkwonVisitor.NodeVisitor<Strikethrough>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Strikethrough strikethrough) {
                final int length = visitor.length();
                visitor.visitChildren(strikethrough);
                visitor.setSpans(length, new StrikethroughSpan());
            }
        });
    }
}
