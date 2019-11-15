package io.noties.markwon.inlineparser;

import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Node;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since 4.2.0
 */
public class NewLineInlineProcessor extends InlineProcessor {

    private static final Pattern FINAL_SPACE = Pattern.compile(" *$");

    @Override
    public char specialCharacter() {
        return '\n';
    }

    @Override
    protected Node parse() {
        index++; // assume we're at a \n

        final Node previous = block.getLastChild();

        // Check previous text for trailing spaces.
        // The "endsWith" is an optimization to avoid an RE match in the common case.
        if (previous instanceof Text && ((Text) previous).getLiteral().endsWith(" ")) {
            Text text = (Text) previous;
            String literal = text.getLiteral();
            Matcher matcher = FINAL_SPACE.matcher(literal);
            int spaces = matcher.find() ? matcher.end() - matcher.start() : 0;
            if (spaces > 0) {
                text.setLiteral(literal.substring(0, literal.length() - spaces));
            }
            if (spaces >= 2) {
                return new HardLineBreak();
            } else {
                return new SoftLineBreak();
            }
        } else {
            return new SoftLineBreak();
        }
    }
}
