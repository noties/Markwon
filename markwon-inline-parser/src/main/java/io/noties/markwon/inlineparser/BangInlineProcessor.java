package io.noties.markwon.inlineparser;

import org.commonmark.internal.Bracket;
import org.commonmark.node.Text;

/**
 * Parses markdown images {@code ![alt](#href)}
 *
 * @since 4.2.0-SNAPSHOT
 */
public class BangInlineProcessor extends InlineProcessor {
    @Override
    public char specialCharacter() {
        return '!';
    }

    @Override
    protected boolean parse() {
        int startIndex = index;
        index++;
        if (peek() == '[') {
            index++;

            Text node = appendText("![");

            // Add entry to stack for this opener
            addBracket(Bracket.image(node, startIndex + 1, lastBracket(), lastDelimiter()));
        } else {
            appendText("!");
        }
        return true;
    }
}
