package io.noties.markwon.ext.latex;

import org.commonmark.internal.util.Parsing;
import org.commonmark.node.Block;
import org.commonmark.parser.block.AbstractBlockParser;
import org.commonmark.parser.block.AbstractBlockParserFactory;
import org.commonmark.parser.block.BlockContinue;
import org.commonmark.parser.block.BlockStart;
import org.commonmark.parser.block.MatchedBlockParser;
import org.commonmark.parser.block.ParserState;

public class JLatexMathBlockParser extends AbstractBlockParser {

    private final JLatexMathBlock block = new JLatexMathBlock();

    private final StringBuilder builder = new StringBuilder();

    private boolean isClosed;

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockContinue tryContinue(ParserState parserState) {

        if (isClosed) {
            return BlockContinue.finished();
        }

        return BlockContinue.atIndex(parserState.getIndex());
    }

    @Override
    public void addLine(CharSequence line) {

        if (builder.length() > 0) {
            builder.append('\n');
        }

        builder.append(line);

        final int length = builder.length();
        if (length > 1) {
            isClosed = '$' == builder.charAt(length - 1)
                    && '$' == builder.charAt(length - 2);
            if (isClosed) {
                builder.replace(length - 2, length, "");
            }
        }
    }

    @Override
    public void closeBlock() {
        block.latex(builder.toString());
    }

    public static class Factory extends AbstractBlockParserFactory {

        @Override
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {

            final int indent = state.getIndent();

            // check if it's an indented code block
            if (indent < Parsing.CODE_BLOCK_INDENT) {
                final int nextNonSpaceIndex = state.getNextNonSpaceIndex();
                final CharSequence line = state.getLine();
                final int length = line.length();
                // we are looking for 2 `$$` subsequent signs
                // and immediate new-line or arbitrary number of white spaces (we check for the first one)
                // so, nextNonSpaceIndex + 2 >= length and both symbols are `$`s
                final int diff = length - (nextNonSpaceIndex + 2);
                if (diff >= 0) {
                    // check for both `$`
                    if (line.charAt(nextNonSpaceIndex) == '$'
                            && line.charAt(nextNonSpaceIndex + 1) == '$') {

                        if (diff > 0) {
                            if (!Character.isWhitespace(line.charAt(nextNonSpaceIndex + 2))) {
                                return BlockStart.none();
                            }
                            // consume all until new-line or first not-white-space char
                        }

                    }
                }
            }

            return BlockStart.none();
        }
    }
}
