package io.noties.markwon.ext.latex;

import androidx.annotation.NonNull;

import org.commonmark.internal.util.Parsing;
import org.commonmark.node.Block;
import org.commonmark.parser.block.AbstractBlockParser;
import org.commonmark.parser.block.AbstractBlockParserFactory;
import org.commonmark.parser.block.BlockContinue;
import org.commonmark.parser.block.BlockStart;
import org.commonmark.parser.block.MatchedBlockParser;
import org.commonmark.parser.block.ParserState;

/**
 * @since 4.3.0 (although there was a class with the same name,
 * which is renamed now to {@link JLatexMathBlockParserLegacy})
 */
class JLatexMathBlockParser extends AbstractBlockParser {

    private static final char DOLLAR = '$';
    private static final char SPACE = ' ';

    private final JLatexMathBlock block = new JLatexMathBlock();

    private final StringBuilder builder = new StringBuilder();

    private final int signs;

    JLatexMathBlockParser(int signs) {
        this.signs = signs;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockContinue tryContinue(ParserState parserState) {
        final int nextNonSpaceIndex = parserState.getNextNonSpaceIndex();
        final CharSequence line = parserState.getLine();
        final int length = line.length();

        // check for closing
        if (parserState.getIndent() < Parsing.CODE_BLOCK_INDENT) {
            if (consume(DOLLAR, line, nextNonSpaceIndex, length) == signs) {
                // okay, we have our number of signs
                // let's consume spaces until the end
                if (Parsing.skip(SPACE, line, nextNonSpaceIndex + signs, length) == length) {
                    return BlockContinue.finished();
                }
            }
        }

        return BlockContinue.atIndex(parserState.getIndex());
    }

    @Override
    public void addLine(CharSequence line) {
        builder.append(line);
        builder.append('\n');
    }

    @Override
    public void closeBlock() {
        block.latex(builder.toString());
    }

    public static class Factory extends AbstractBlockParserFactory {

        @Override
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {

            // let's define the spec:
            //  * 0-3 spaces before are allowed (Parsing.CODE_BLOCK_INDENT = 4)
            //  * 2+ subsequent `$` signs
            //  * any optional amount of spaces
            //  * new line
            //  * block is closed when the same amount of opening signs is met

            final int indent = state.getIndent();

            // check if it's an indented code block
            if (indent >= Parsing.CODE_BLOCK_INDENT) {
                return BlockStart.none();
            }

            final int nextNonSpaceIndex = state.getNextNonSpaceIndex();
            final CharSequence line = state.getLine();
            final int length = line.length();

            final int signs = consume(DOLLAR, line, nextNonSpaceIndex, length);

            // 2 is minimum
            if (signs < 2) {
                return BlockStart.none();
            }

            // consume spaces until the end of the line, if any other content is found -> NONE
            if (Parsing.skip(SPACE, line, nextNonSpaceIndex + signs, length) != length) {
                return BlockStart.none();
            }

            return BlockStart.of(new JLatexMathBlockParser(signs))
                    .atIndex(length + 1);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static int consume(char c, @NonNull CharSequence line, int start, int end) {
        for (int i = start; i < end; i++) {
            if (c != line.charAt(i)) {
                return i - start;
            }
        }
        // all consumed
        return end - start;
    }
}
