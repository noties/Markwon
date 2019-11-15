package io.noties.markwon.inlineparser;

import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

import java.util.LinkedList;
import java.util.ListIterator;

class StaggeredDelimiterProcessor implements DelimiterProcessor {

    private final char delim;
    private int minLength = 0;
    private LinkedList<DelimiterProcessor> processors = new LinkedList<>(); // in reverse getMinLength order

    StaggeredDelimiterProcessor(char delim) {
        this.delim = delim;
    }

    @Override
    public char getOpeningCharacter() {
        return delim;
    }

    @Override
    public char getClosingCharacter() {
        return delim;
    }

    @Override
    public int getMinLength() {
        return minLength;
    }

    void add(DelimiterProcessor dp) {
        final int len = dp.getMinLength();
        ListIterator<DelimiterProcessor> it = processors.listIterator();
        boolean added = false;
        while (it.hasNext()) {
            DelimiterProcessor p = it.next();
            int pLen = p.getMinLength();
            if (len > pLen) {
                it.previous();
                it.add(dp);
                added = true;
                break;
            } else if (len == pLen) {
                throw new IllegalArgumentException("Cannot add two delimiter processors for char '" + delim + "' and minimum length " + len);
            }
        }
        if (!added) {
            processors.add(dp);
            this.minLength = len;
        }
    }

    private DelimiterProcessor findProcessor(int len) {
        for (DelimiterProcessor p : processors) {
            if (p.getMinLength() <= len) {
                return p;
            }
        }
        return processors.getFirst();
    }

    @Override
    public int getDelimiterUse(DelimiterRun opener, DelimiterRun closer) {
        return findProcessor(opener.length()).getDelimiterUse(opener, closer);
    }

    @Override
    public void process(Text opener, Text closer, int delimiterUse) {
        findProcessor(delimiterUse).process(opener, closer, delimiterUse);
    }
}
