package io.noties.markwon;

import androidx.annotation.NonNull;

import org.commonmark.node.SoftLineBreak;

/**
 * @since 4.3.0
 */
public class SoftBreakAddsNewLinePlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static SoftBreakAddsNewLinePlugin create() {
        return new SoftBreakAddsNewLinePlugin();
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(SoftLineBreak.class, new MarkwonVisitor.NodeVisitor<SoftLineBreak>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull SoftLineBreak softLineBreak) {
                visitor.ensureNewLine();
            }
        });
    }
}
