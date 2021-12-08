package io.noties.markwon.ext.inline;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import org.commonmark.parser.Parser;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.LinkResolver;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.core.spans.LinkSpan;

public class InLineLinkPlugIn extends AbstractMarkwonPlugin {

    @NonNull
    public static InLineLinkPlugIn create() {
        return new InLineLinkPlugIn();
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.customDelimiterProcessor(InLineLinkProcessor.create());
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(InLineLinkNode.class, new MarkwonVisitor.NodeVisitor<InLineLinkNode>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull InLineLinkNode inLineLinkNode) {

                final String link = inLineLinkNode.link();
                if (!TextUtils.isEmpty(link)) {
                    final int length = visitor.length();
                    visitor.builder().append(link);
                    visitor.setSpans(length, new LinkSpan(visitor.configuration().theme(), link, new LinkResolver() {
                        @Override
                        public void resolve(@NonNull View view, @NonNull String link) {
                        }
                    }));
                    visitor.builder().append(' ');
                }
            }
        });
    }

    @NonNull
    @Override
    public String processMarkdown(@NonNull String markdown) {
        return InLineLinkProcessor.prepare(markdown);
    }
}
