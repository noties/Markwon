package io.noties.markwon.sample.editor.inline;

import androidx.annotation.NonNull;

import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Node;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewLineInline extends Inline {

    private static final Pattern FINAL_SPACE = Pattern.compile(" *$");

    @NonNull
    @Override
    public Collection<Character> characters() {
        return Collections.singleton('\n');
    }

    @Override
    public boolean parse() {
        index++; // assume we're at a \n

        Node lastChild = block.getLastChild();
        // Check previous text for trailing spaces.
        // The "endsWith" is an optimization to avoid an RE match in the common case.
        if (lastChild != null && lastChild instanceof Text && ((Text) lastChild).getLiteral().endsWith(" ")) {
            Text text = (Text) lastChild;
            String literal = text.getLiteral();
            Matcher matcher = FINAL_SPACE.matcher(literal);
            int spaces = matcher.find() ? matcher.end() - matcher.start() : 0;
            if (spaces > 0) {
                text.setLiteral(literal.substring(0, literal.length() - spaces));
            }
            appendNode(spaces >= 2 ? new HardLineBreak() : new SoftLineBreak());
        } else {
            appendNode(new SoftLineBreak());
        }

        // gobble leading spaces in next line
        while (peek() == ' ') {
            index++;
        }
        return true;
    }
}
