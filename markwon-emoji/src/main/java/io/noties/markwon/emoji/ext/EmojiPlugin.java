package io.noties.markwon.emoji.ext;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.commonmark.parser.Parser;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonVisitor;

public class EmojiPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static EmojiPlugin create(@NonNull EmojiSpanProvider emojiSpanProvider) {
        return new EmojiPlugin(emojiSpanProvider);
    }

    private final EmojiSpanProvider emojiSpanProvider;

    EmojiPlugin(@NonNull EmojiSpanProvider emojiSpanProvider) {
        this.emojiSpanProvider = emojiSpanProvider;
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.customDelimiterProcessor(EmojiProcessor.create());
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(EmojiNode.class, new MarkwonVisitor.NodeVisitor<EmojiNode>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull EmojiNode emojiNode) {

                final String colonCode = emojiNode.getColonCode();

                if (!TextUtils.isEmpty(colonCode)) {

                    final int length = visitor.length();
                    visitor.builder().append(colonCode);
                    visitor.setSpans(length, emojiSpanProvider.provide(colonCode));
                    visitor.builder().append(' ');
                }
            }
        });
    }

    @NonNull
    @Override
    public String processMarkdown(@NonNull String markdown) {
        return EmojiProcessor.prepare(markdown);
    }
}
