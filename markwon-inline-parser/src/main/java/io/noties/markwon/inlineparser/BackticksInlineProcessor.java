package io.noties.markwon.inlineparser;

import org.commonmark.node.Code;

import java.util.regex.Pattern;

/**
 * Parses inline code surrounded with {@code `} chars {@code `code`}
 *
 * @since 4.2.0-SNAPSHOT
 */
public class BackticksInlineProcessor extends InlineProcessor {

    private static final Pattern TICKS = Pattern.compile("`+");

    private static final Pattern TICKS_HERE = Pattern.compile("^`+");

    private static final Pattern WHITESPACE = MarkwonInlineParser.WHITESPACE;

    @Override
    public char specialCharacter() {
        return '`';
    }

    @Override
    protected boolean parse() {
        String ticks = match(TICKS_HERE);
        if (ticks == null) {
            return false;
        }
        int afterOpenTicks = index;
        String matched;
        while ((matched = match(TICKS)) != null) {
            if (matched.equals(ticks)) {
                Code node = new Code();
                String content = input.substring(afterOpenTicks, index - ticks.length());
                String literal = WHITESPACE.matcher(content.trim()).replaceAll(" ");
                node.setLiteral(literal);
                appendNode(node);
                return true;
            }
        }
        // If we got here, we didn't match a closing backtick sequence.
        index = afterOpenTicks;
        appendText(ticks);
        return true;
    }
}
