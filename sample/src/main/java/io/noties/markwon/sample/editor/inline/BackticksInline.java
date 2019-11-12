package io.noties.markwon.sample.editor.inline;

import androidx.annotation.NonNull;

import org.commonmark.node.Code;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public class BackticksInline extends Inline {

    private static final Pattern TICKS = Pattern.compile("`+");

    private static final Pattern TICKS_HERE = Pattern.compile("^`+");

    @NonNull
    @Override
    public Collection<Character> characters() {
        return Collections.singleton('`');
    }

    @Override
    public boolean parse() {
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
