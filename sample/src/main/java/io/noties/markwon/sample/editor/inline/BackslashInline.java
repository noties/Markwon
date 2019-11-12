package io.noties.markwon.sample.editor.inline;

import androidx.annotation.NonNull;

import org.commonmark.node.HardLineBreak;

import java.util.Collection;
import java.util.Collections;

public class BackslashInline extends Inline {
    @NonNull
    @Override
    public Collection<Character> characters() {
        return Collections.singleton('\\');
    }

    @Override
    public boolean parse() {
        index++;
        if (peek() == '\n') {
            appendNode(new HardLineBreak());
            index++;
        } else if (index < input.length() && ESCAPABLE.matcher(input.substring(index, index + 1)).matches()) {
            appendText(input, index, index + 1);
            index++;
        } else {
            appendText("\\");
        }
        return true;
    }
}
