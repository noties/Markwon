package ru.noties.markwon.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.Collections;

import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.renderer.SpannableRenderer;

public class MarkdownTextView extends TextView {

    private Parser parser;
    private SpannableRenderer renderer;
    private SpannableConfiguration configuration;

    public MarkdownTextView(Context context) {
        super(context);
    }

    public MarkdownTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MarkdownTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MarkdownTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (parser == null) {
            parser = Parser.builder()
                    .extensions(Collections.singletonList(StrikethroughExtension.create()))
                    .build();
        }
        if (renderer == null) {
            renderer = new SpannableRenderer();
        }
        if (configuration == null) {
            configuration = SpannableConfiguration.create(getContext());
        }
        final Node node = parser.parse(text.toString());
        final CharSequence cs = renderer.render(configuration, node);
        super.setText(cs, type);
    }
}
