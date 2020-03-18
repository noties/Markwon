package io.noties.markwon.ext.latex;

import org.commonmark.node.Block;
import org.commonmark.parser.block.AbstractBlockParser;
import org.commonmark.parser.block.AbstractBlockParserFactory;
import org.commonmark.parser.block.BlockContinue;
import org.commonmark.parser.block.BlockStart;
import org.commonmark.parser.block.MatchedBlockParser;
import org.commonmark.parser.block.ParserState;

/**
 * @since 4.3.0 (although it is just renamed parser from previous versions)
 */
class JLatexMathBlockParserLegacy extends AbstractBlockParser {

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

            final CharSequence line = state.getLine();
            final int length = line != null
                    ? line.length()
                    : 0;

            if (length > 1) {
                if ('$' == line.charAt(0)
                        && '$' == line.charAt(1)) {
                    return BlockStart.of(new JLatexMathBlockParserLegacy())
                            .atIndex(state.getIndex() + 2);
                }
            }

            return BlockStart.none();
        }
    }
}
