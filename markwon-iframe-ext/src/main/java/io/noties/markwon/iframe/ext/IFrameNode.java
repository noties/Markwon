package io.noties.markwon.iframe.ext;

import androidx.annotation.NonNull;

import org.commonmark.node.CustomNode;

public class IFrameNode extends CustomNode {

    public static final String DELIMITER_STRING = "![";
    private final String link;
    public IFrameNode(@NonNull String link) {
        this.link = link;
    }

    @NonNull
    public String link() {
        return link;
    }
}