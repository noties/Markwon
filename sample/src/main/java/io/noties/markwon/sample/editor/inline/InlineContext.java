package io.noties.markwon.sample.editor.inline;

import org.commonmark.internal.Bracket;
import org.commonmark.internal.Delimiter;
import org.commonmark.node.Link;
import org.commonmark.parser.delimiter.DelimiterProcessor;

import java.util.Map;

public class InlineContext {

    /**
     * Top delimiter (emphasis, strong emphasis or custom emphasis). (Brackets are on a separate stack, different
     * from the algorithm described in the spec.)
     */
    private Delimiter lastDelimiter;

    /**
     * Top opening bracket (<code>[</code> or <code>![)</code>).
     */
    private Bracket lastBracket;

    /**
     * Link references by ID, needs to be built up using parseReference before calling parse.
     */
    private Map<String, Link> referenceMap;

    private Map<Character, DelimiterProcessor> delimiterProcessors;


    public Delimiter lastDelimiter() {
        return lastDelimiter;
    }

    public void lastDelimiter(Delimiter lastDelimiter) {
        this.lastDelimiter = lastDelimiter;
    }

    public Bracket lastBracket() {
        return lastBracket;
    }

    public void lastBracket(Bracket lastBracket) {
        this.lastBracket = lastBracket;
    }

    public Map<String, Link> referenceMap() {
        return referenceMap;
    }

    public void referenceMap(Map<String, Link> referenceMap) {
        this.referenceMap = referenceMap;
    }

    public Map<Character, DelimiterProcessor> delimiterProcessors() {
        return delimiterProcessors;
    }

    public void delimiterProcessors(Map<Character, DelimiterProcessor> delimiterProcessors) {
        this.delimiterProcessors = delimiterProcessors;
    }
}
