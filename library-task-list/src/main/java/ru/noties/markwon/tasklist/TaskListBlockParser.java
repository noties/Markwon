package ru.noties.markwon.tasklist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Block;
import org.commonmark.parser.InlineParser;
import org.commonmark.parser.block.AbstractBlockParser;
import org.commonmark.parser.block.AbstractBlockParserFactory;
import org.commonmark.parser.block.BlockContinue;
import org.commonmark.parser.block.BlockStart;
import org.commonmark.parser.block.MatchedBlockParser;
import org.commonmark.parser.block.ParserState;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.noties.debug.Debug;

class TaskListBlockParser extends AbstractBlockParser {

    private static final Pattern PATTERN = Pattern.compile("\\s*-\\s+\\[(x|X|\\s)\\]\\s+(.*)");
//    private static final Pattern PATTERN_2 = Pattern.compile("^\\s*-\\s+\\[(x|X|\\s)\\]\\s+(.*)");

    private final TaskListBlock block = new TaskListBlock();

    private final List<String> lines;

    TaskListBlockParser(@NonNull String startLine) {
        this.lines = new ArrayList<>(3);
        this.lines.add(startLine);
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockContinue tryContinue(ParserState parserState) {

        final BlockContinue blockContinue;

        final String line = line(parserState);

//        Debug.i("line: %s, find: %s", line, PATTERN.matcher(line).find());
        Debug.i("isBlank: %s, line: `%s`", parserState.isBlank(), line);

        if (line != null
                && line.length() > 0
                && PATTERN.matcher(line).matches()) {
            Debug.e();
            blockContinue = BlockContinue.atIndex(parserState.getIndex());
        } else {
            Debug.e();
            blockContinue = BlockContinue.finished();
        }

        return blockContinue;
    }

    @Override
    public void addLine(CharSequence line) {
        Debug.i("line: %s", line);
        if (line != null
                && line.length() > 0) {
            lines.add(line.toString());
        }
    }

    @Override
    public void parseInlines(InlineParser inlineParser) {

        Debug.i(lines);

        Matcher matcher;

        TaskListItem item;

        for (String line : lines) {

            matcher = PATTERN.matcher(line);

            if (!matcher.matches()) {
                continue;
            }

            item = new TaskListItem().done(isDone(matcher.group(1)));

            inlineParser.parse(matcher.group(2), item);

            block.appendChild(item);
        }

    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public boolean canContain(Block block) {
        Debug.i("block: %s", block);
        return false;
    }

    @Override
    public void closeBlock() {
        Debug.e(block);
        Debug.trace();
    }

    static class Factory extends AbstractBlockParserFactory {

        @Override
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {

            final String line = line(state);

            if (line != null
                    && line.length() > 0
                    && PATTERN.matcher(line).matches()) {

                return BlockStart.of(new TaskListBlockParser(line))
                        .atIndex(state.getIndex() + line.length());
            }

            return BlockStart.none();
        }
    }

    @Nullable
    private static String line(@NonNull ParserState state) {
        final CharSequence lineRaw = state.getLine();
        return lineRaw != null
                ? lineRaw.toString()
                : null;
    }

    private static boolean isDone(@NonNull String value) {
        return "X".equals(value)
                || "x".equals(value);
    }
}
