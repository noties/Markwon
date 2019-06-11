package io.noties.markwon.ext.strikethrough;

import android.text.style.StrikethroughSpan;

import androidx.annotation.NonNull;

import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.parser.Parser;

import java.util.Collections;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;

/**
 * Plugin to add strikethrough markdown feature. This plugin will extend commonmark-java.Parser
 * with strikethrough extension, add SpanFactory and register commonmark-java.Strikethrough node
 * visitor
 *
 * @see #create()
 * @since 3.0.0
 */
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
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        builder.setFactory(Strikethrough.class, new SpanFactory() {
            @Override
            public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                return new StrikethroughSpan();
            }
        });
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Strikethrough.class, new MarkwonVisitor.NodeVisitor<Strikethrough>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Strikethrough strikethrough) {
                final int length = visitor.length();
                visitor.visitChildren(strikethrough);
                visitor.setSpansForNodeOptional(strikethrough, length);
            }
        });
    }
}
