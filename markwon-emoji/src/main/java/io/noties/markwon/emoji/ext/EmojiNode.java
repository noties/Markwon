package io.noties.markwon.emoji.ext;
import androidx.annotation.NonNull;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;

@SuppressWarnings("WeakerAccess")
public class EmojiNode extends CustomNode implements Delimited {

    public static final char DELIMITER = ':';

    public static final String DELIMITER_STRING = "" + DELIMITER;


    private final String colonCode;


    public EmojiNode(@NonNull String colonCode) {
        this.colonCode = colonCode;

    }

    @NonNull
    public String getColonCode() {
        return colonCode;
    }

    @Override
    public String getOpeningDelimiter() {
        return DELIMITER_STRING;
    }

    @Override
    public String getClosingDelimiter() {
        return DELIMITER_STRING;
    }
}
