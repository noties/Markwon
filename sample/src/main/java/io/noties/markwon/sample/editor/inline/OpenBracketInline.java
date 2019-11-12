package io.noties.markwon.sample.editor.inline;

import androidx.annotation.NonNull;

import org.commonmark.internal.Bracket;
import org.commonmark.node.Text;

import java.util.Collection;
import java.util.Collections;

public class OpenBracketInline extends Inline {
    @NonNull
    @Override
    public Collection<Character> characters() {
        return Collections.singleton('[');
    }

    @Override
    public boolean parse() {

        int startIndex = index;
        index++;

        Text node = appendText("[");

        // Add entry to stack for this opener
        addBracket(Bracket.link(node, startIndex, lastBracket(), lastDelimiter()));

        return true;
    }
}
