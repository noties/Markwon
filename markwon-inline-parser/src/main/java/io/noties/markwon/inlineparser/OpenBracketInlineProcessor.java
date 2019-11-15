package io.noties.markwon.inlineparser;

import org.commonmark.internal.Bracket;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

/**
 * Parses markdown links {@code [link](#href)}
 *
 * @since 4.2.0
 */
public class OpenBracketInlineProcessor extends InlineProcessor {
    @Override
    public char specialCharacter() {
        return '[';
    }

    @Override
    protected Node parse() {
        int startIndex = index;
        index++;

        Text node = text("[");

        // Add entry to stack for this opener
        addBracket(Bracket.link(node, startIndex, lastBracket(), lastDelimiter()));

        return node;
    }
}
