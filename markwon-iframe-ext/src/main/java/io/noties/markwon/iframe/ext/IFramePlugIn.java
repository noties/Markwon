package io.noties.markwon.iframe.ext;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.commonmark.parser.Parser;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonVisitor;

public class IFramePlugIn extends AbstractMarkwonPlugin {

    @NonNull
    public static IFramePlugIn create() {
        return new IFramePlugIn();
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.customDelimiterProcessor(IFrameProcessor.create());
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(IFrameNode.class, new MarkwonVisitor.NodeVisitor<IFrameNode>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull IFrameNode iFrameNode) {

                final String link = iFrameNode.link();
                if (!TextUtils.isEmpty(link)) {
                    visitor.builder().append(link);
                    visitor.builder().append(' ');
                }
            }
        });
    }

    @NonNull
    @Override
    public String processMarkdown(@NonNull String markdown) {
        return IFrameProcessor.prepare(markdown);
    }
}
