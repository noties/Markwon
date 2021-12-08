package io.noties.markwon.ext.inline;

import androidx.annotation.NonNull;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;

@SuppressWarnings("WeakerAccess")
public class InLineLinkNode extends CustomNode implements Delimited {

    public static final String DELIMITER_STRING = "#";


    private final String link;

    public InLineLinkNode(@NonNull String link) {
        this.link = link;
    }

    @NonNull
    public String link() {
        return link;
    }


    @Override
    public String getOpeningDelimiter() {
        return DELIMITER_STRING;
    }

    @Override
    public String getClosingDelimiter() {
        return DELIMITER_STRING;
    }

    @Override
    public String toString() {
        return "InLineLinkNode{" +
                "link='" + link +'\'' + '\"' + '}';
    }
}
