package io.noties.markwon.inlineparser;

import org.commonmark.internal.Bracket;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

/**
 * Parses markdown images {@code ![alt](#href)}
 *
 * @since 4.2.0
 */
public class BangInlineProcessor extends InlineProcessor {
    @Override
    public char specialCharacter() {
        return '!';
    }

    @Override
    protected Node parse() {
        int startIndex = index;
        index++;
        if (peek() == '[') {
            index++;

            Text node = text("![");

            // Add entry to stack for this opener
            addBracket(Bracket.image(node, startIndex + 1, lastBracket(), lastDelimiter()));

            return node;
        } else {
            // @since $SNAPSHOT; return null in case no match (multiple inline
            //  processors can define `!` as _special_ character, so let them handle it)
            //  NB! do not forget to reset index
            index = startIndex;
            return null;
        }
    }
}
