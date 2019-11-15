package io.noties.markwon.inlineparser;

import org.commonmark.internal.util.Parsing;
import org.commonmark.node.Code;
import org.commonmark.node.Node;

import java.util.regex.Pattern;

/**
 * Parses inline code surrounded with {@code `} chars {@code `code`}
 *
 * @since 4.2.0
 */
public class BackticksInlineProcessor extends InlineProcessor {

    private static final Pattern TICKS = Pattern.compile("`+");

    private static final Pattern TICKS_HERE = Pattern.compile("^`+");

    @Override
    public char specialCharacter() {
        return '`';
    }

    @Override
    protected Node parse() {
        String ticks = match(TICKS_HERE);
        if (ticks == null) {
            return null;
        }
        int afterOpenTicks = index;
        String matched;
        while ((matched = match(TICKS)) != null) {
            if (matched.equals(ticks)) {
                Code node = new Code();
                String content = input.substring(afterOpenTicks, index - ticks.length());
                content = content.replace('\n', ' ');

                // spec: If the resulting string both begins and ends with a space character, but does not consist
                // entirely of space characters, a single space character is removed from the front and back.
                if (content.length() >= 3 &&
                        content.charAt(0) == ' ' &&
                        content.charAt(content.length() - 1) == ' ' &&
                        Parsing.hasNonSpace(content)) {
                    content = content.substring(1, content.length() - 1);
                }

                node.setLiteral(content);
                return node;
            }
        }
        // If we got here, we didn't match a closing backtick sequence.
        index = afterOpenTicks;
        return text(ticks);
    }
}
