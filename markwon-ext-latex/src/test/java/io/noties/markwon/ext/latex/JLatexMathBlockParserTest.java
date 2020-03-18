package io.noties.markwon.ext.latex;

import androidx.annotation.NonNull;

import org.commonmark.internal.BlockContinueImpl;
import org.commonmark.internal.BlockStartImpl;
import org.commonmark.internal.util.Parsing;
import org.commonmark.parser.block.BlockStart;
import org.commonmark.parser.block.ParserState;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JLatexMathBlockParserTest {

    private static final String[] NO_MATCH = {
            " ",
            "   ",
            "    ",
            "$ ",
            " $ $",
            "-$$",
            "  -$$",
            "$$-",
            " $$  -",
            "  $$        -",
            "$$$          -"
    };

    private static final String[] MATCH = {
            "$$",
            " $$",
            "  $$",
            "   $$",
            "$$                 ",
            " $$   ",
            "  $$       ",
            "   $$                                                    ",
            "$$$",
            " $$$",
            "   $$$",
            "$$$$",
            "   $$$$",
            "$$$$$$$$$$$$$$$$$$$$$",
            " $$$$$$$$$$$$$$$$$$$$$",
            "  $$$$$$$$$$$$$$$$$$$$$",
            "   $$$$$$$$$$$$$$$$$$$$$"
    };

    private JLatexMathBlockParser.Factory factory;

    @Before
    public void before() {
        factory = new JLatexMathBlockParser.Factory();
    }

    @Test
    public void factory_indentBlock() {
        // when state indent is greater than block -> nono

        final ParserState state = mock(ParserState.class);
        when(state.getIndent()).thenReturn(Parsing.CODE_BLOCK_INDENT);

        // hm, interesting, `BlockStart.none()` actually returns null
        final BlockStart start = factory.tryStart(state, null);
        assertNull(start);
    }

    @Test
    public void factory_noMatch() {

        for (String line : NO_MATCH) {
            final ParserState state = createState(line);

            assertNull(factory.tryStart(state, null));
        }
    }

    @Test
    public void factory_match() {

        for (String line : MATCH) {
            final ParserState state = createState(line);

            final BlockStart start = factory.tryStart(state, null);
            assertNotNull(start);

            // hm...
            final BlockStartImpl impl = (BlockStartImpl) start;
            assertEquals(quote(line), line.length() + 1, impl.getNewIndex());
        }
    }

    @Test
    public void finish() {

        for (String line : MATCH) {
            final ParserState state = createState(line);

            // we will have 2 checks here:
            //  * must pass for correct length
            //  * must fail for incorrect

            final int count = countDollarSigns(line);

            // pass
            {
                final JLatexMathBlockParser parser = new JLatexMathBlockParser(count);
                final BlockContinueImpl impl = (BlockContinueImpl) parser.tryContinue(state);
                assertTrue(quote(line), impl.isFinalize());
            }

            // fail (in terms of closing, not failing test)
            {
                final JLatexMathBlockParser parser = new JLatexMathBlockParser(count + 1);
                final BlockContinueImpl impl = (BlockContinueImpl) parser.tryContinue(state);
                assertFalse(quote(line), impl.isFinalize());
            }
        }
    }

    @Test
    public void finish_noMatch() {
        for (String line : NO_MATCH) {
            final ParserState state = createState(line);
            // doesn't matter
            final int count = 2;
            final JLatexMathBlockParser parser = new JLatexMathBlockParser(count);
            final BlockContinueImpl impl = (BlockContinueImpl) parser.tryContinue(state);
            assertFalse(quote(line), impl.isFinalize());
        }
    }

    @NonNull
    private static ParserState createState(@NonNull String line) {

        final ParserState state = mock(ParserState.class);

        int i = 0;
        for (int length = line.length(); i < length; i++) {
            if (' ' != line.charAt(i)) {
                // previous is the last space
                break;
            }
        }

        when(state.getIndent()).thenReturn(i);
        when(state.getNextNonSpaceIndex()).thenReturn(i);
        when(state.getLine()).thenReturn(line);

        return state;
    }

    private static int countDollarSigns(@NonNull String line) {
        int count = 0;
        for (int i = 0, length = line.length(); i < length; i++) {
            if ('$' == line.charAt(i)) count += 1;
        }
        return count;
    }

    @NonNull
    private static String quote(@NonNull String s) {
        return '\'' + s + '\'';
    }
}