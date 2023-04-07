package io.noties.markwon.simple.ext;

import androidx.annotation.NonNull;

import com.vladsch.flexmark.parser.InlineParser;
import com.vladsch.flexmark.parser.core.delimiter.Delimiter;
import com.vladsch.flexmark.parser.delimiter.DelimiterProcessor;
import com.vladsch.flexmark.parser.delimiter.DelimiterRun;
import com.vladsch.flexmark.util.ast.Node;

import io.noties.markwon.SpanFactory;

// @since 4.0.0
class SimpleExtDelimiterProcessor implements DelimiterProcessor {

    private final char open;
    private final char close;
    private final int length;
    private final SpanFactory spanFactory;

    SimpleExtDelimiterProcessor(
            char open,
            char close,
            int length,
            @NonNull SpanFactory spanFactory) {
        this.open = open;
        this.close = close;
        this.length = length;
        this.spanFactory = spanFactory;
    }

    @Override
    public char getOpeningCharacter() {
        return open;
    }

    @Override
    public char getClosingCharacter() {
        return close;
    }

    @Override
    public int getMinLength() {
        return length;
    }

    @Override
    public int getDelimiterUse(DelimiterRun opener, DelimiterRun closer) {
        if (opener.length() >= length && closer.length() >= length) {
            return length;
        }
        return 0;
    }

    @Override
    public void process(Delimiter opener, Delimiter closer, int delimitersUsed) {
        final Node node = new SimpleExtNode(spanFactory);
        Delimiter tmp = opener.getNext();
        Delimiter next;

        while (tmp != null && tmp != closer) {
            next = tmp.getNext();
            node.appendChild(tmp.getNode());
            tmp = next;
        }

        opener.getNode().insertAfter(node);
    }

    @Override
    public Node unmatchedDelimiterNode(InlineParser inlineParser, DelimiterRun delimiter) {
        return null;
    }

    @Override
    public boolean canBeOpener(String before, String after, boolean leftFlanking, boolean rightFlanking, boolean beforeIsPunctuation, boolean afterIsPunctuation, boolean beforeIsWhitespace, boolean afterIsWhiteSpace) {
        return false;
    }

    @Override
    public boolean canBeCloser(String before, String after, boolean leftFlanking, boolean rightFlanking, boolean beforeIsPunctuation, boolean afterIsPunctuation, boolean beforeIsWhitespace, boolean afterIsWhiteSpace) {
        return false;
    }

    @Override
    public boolean skipNonOpenerCloser() {
        return false;
    }
}
