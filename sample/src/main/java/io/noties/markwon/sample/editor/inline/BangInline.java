package io.noties.markwon.sample.editor.inline;

import androidx.annotation.NonNull;

import org.commonmark.internal.Bracket;
import org.commonmark.node.Text;

import java.util.Collection;
import java.util.Collections;

public class BangInline extends Inline {
    @NonNull
    @Override
    public Collection<Character> characters() {
        return Collections.singleton('!');
    }

    @Override
    public boolean parse() {
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
