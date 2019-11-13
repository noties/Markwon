package io.noties.markwon.inlineparser;

import org.commonmark.node.HardLineBreak;

import java.util.regex.Pattern;

/**
 * @since 4.2.0-SNAPSHOT
 */
public class BackslashInlineProcessor extends InlineProcessor {

    private static final Pattern ESCAPABLE = MarkwonInlineParser.ESCAPABLE;

    @Override
    public char specialCharacter() {
        return '\\';
    }

    @Override
    protected boolean parse() {
        index++;
        if (peek() == '\n') {
            appendNode(new HardLineBreak());
            index++;
        } else if (index < input.length() && ESCAPABLE.matcher(input.substring(index, index + 1)).matches()) {
            appendText(input, index, index + 1);
            index++;
        } else {
            appendText("\\");
        }
        return true;
    }
}
