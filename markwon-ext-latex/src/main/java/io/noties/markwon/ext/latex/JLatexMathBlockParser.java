package io.noties.markwon.ext.latex;

import android.util.Log;

import androidx.annotation.NonNull;

import org.commonmark.internal.util.Parsing;
import org.commonmark.node.Block;
import org.commonmark.parser.block.AbstractBlockParser;
import org.commonmark.parser.block.AbstractBlockParserFactory;
import org.commonmark.parser.block.BlockContinue;
import org.commonmark.parser.block.BlockStart;
import org.commonmark.parser.block.MatchedBlockParser;
import org.commonmark.parser.block.ParserState;

public class JLatexMathBlockParser extends AbstractBlockParser {

    private static final char DOLLAR = '$';
    private static final char SPACE = ' ';

    private final JLatexMathBlock block = new JLatexMathBlock();

    private final StringBuilder builder = new StringBuilder();

//    private boolean isClosed;

    private final int signs;

    @SuppressWarnings("WeakerAccess")
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
            Log.e("LTX", String.format("signs: %d, skip dollar: %s", signs, Parsing.skip(DOLLAR, line, nextNonSpaceIndex, length)));
//            if (Parsing.skip(DOLLAR, line, nextNonSpaceIndex, length) == signs) {
            if (consume(DOLLAR, line, nextNonSpaceIndex, length) == signs) {
                // okay, we have our number of signs
                // let's consume spaces until the end
                Log.e("LTX", String.format("length; %d, skip spaces: %s", length, Parsing.skip(SPACE, line, nextNonSpaceIndex + signs, length)));
                if (Parsing.skip(SPACE, line, nextNonSpaceIndex + signs, length) == length) {
                    return BlockContinue.finished();
                }
            }
        }

        return BlockContinue.atIndex(parserState.getIndex());
    }

    @Override
    public void addLine(CharSequence line) {
//
//        if (builder.length() > 0) {
//            builder.append('\n');
//        }
//
//        builder.append(line);
//
//        final int length = builder.length();
//        if (length > 1) {
//            isClosed = '$' == builder.charAt(length - 1)
//                    && '$' == builder.charAt(length - 2);
//            if (isClosed) {
//                builder.replace(length - 2, length, "");
//            }
//        }
        Log.e("LTX", "addLine: " + line);
        builder.append(line);
        builder.append('\n');
    }

    @Override
    public void closeBlock() {
        block.latex(builder.toString());
    }

    public static class Factory extends AbstractBlockParserFactory {

//        private static final Pattern RE = Pattern.compile("(\\${2,}) *$");

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

//            final int signs = Parsing.skip(DOLLAR, line, nextNonSpaceIndex, length) - 1;
            final int signs = consume(DOLLAR, line, nextNonSpaceIndex, length);

            // 2 is minimum
            if (signs < 2) {
                return BlockStart.none();
            }

            // consume spaces until the end of the line, if any other content is found -> NONE
            if (Parsing.skip(SPACE, line, nextNonSpaceIndex + signs, length) != length) {
                return BlockStart.none();
            }

            Log.e("LTX", String.format("signs: %s, next: %d, length: %d, line: '%s'", signs, nextNonSpaceIndex, length, line));

            return BlockStart.of(new JLatexMathBlockParser(signs))
                    .atIndex(length + 1);


//            // check if it's an indented code block
//            if (indent < Parsing.CODE_BLOCK_INDENT) {
//
//                final int nextNonSpaceIndex = state.getNextNonSpaceIndex();
//                final CharSequence line = state.getLine();
//                final int length = line.length();
//
//                final int signs = Parsing.skip('$', line, nextNonSpaceIndex, length);
//
//                // 2 is minimum
//                if (signs < 2) {
//                    return BlockStart.none();
//                }
//
//                // consume spaces until the end of the line, if any other content is found -> NONE
//                if (Parsing.skip(' ', line, nextNonSpaceIndex + signs, length) != length) {
//                    return BlockStart.none();
//                }
//
////                // consume spaces until the end of the line, if any other content is found -> NONE
////                if ((nextNonSpaceIndex + signs) < length) {
////                    // check if more content is available
////                    if (Parsing.skip(' ',  line,nextNonSpaceIndex + signs, length) != length) {
////                        return BlockStart.none();
////                    }
////                }
//
////                final Matcher matcher = RE.matcher(line);
////                matcher.region(nextNonSpaceIndex, length);
//
////                Log.e("LATEX", String.format("nonSpace: %d, length: %s, line: '%s'", nextNonSpaceIndex, length, line));
//
//                // we are looking for 2 `$$` subsequent signs
//                // and immediate new-line or arbitrary number of white spaces (we check for the first one)
//                // so, nextNonSpaceIndex + 2 >= length and both symbols are `$`s
//                final int diff = length - (nextNonSpaceIndex + 2);
//                if (diff >= 0) {
//                    // check for both `$`
//                    if (line.charAt(nextNonSpaceIndex) == '$'
//                            && line.charAt(nextNonSpaceIndex + 1) == '$') {
//
//                        if (diff > 0) {
//                            if (!Character.isWhitespace(line.charAt(nextNonSpaceIndex + 2))) {
//                                return BlockStart.none();
//                            }
//                            return BlockStart.of(new JLatexMathBlockParser()).atIndex(nextNonSpaceIndex + 3);
//                        }
//
//                    }
//                }
//            }
//
//            return BlockStart.none();
        }
    }

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
