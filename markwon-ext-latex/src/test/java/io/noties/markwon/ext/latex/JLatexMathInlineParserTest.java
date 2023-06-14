package io.noties.markwon.ext.latex;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.regex.Matcher;

/**
 * @author YvesCheung
 * 2023/6/14
 */
@RunWith(Parameterized.class)
public class JLatexMathInlineParserTest {

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][]{
            {true, "$ a $", "$ a $", " a "},
            {false, "$ a $", "", ""},

            {true, "ashdksa $ a $sfalhs", "$ a $", " a "},
            {false, "ashdksa $ a $sfalhs", "", ""},

            {true, "c sajdl asjkdhk $abc$\n sahdila", "$abc$", "abc"},
            {false, "c sajdl asjkdhk $abc$\n sahdila", "", ""},

            {true, "c sajdl asjkdhk \n$abcd $ sahdila", "$abcd $", "abcd "},
            {false, "c sajdl asjkdhk \n$abcd $ sahdila", "", ""},

            {true, "$$abc$$", "$$abc$$", "abc"},
            {false, "$$abc$$", "$$abc$$", "abc"},

            {true, "$$abc$", "$$abc$", "$abc"},
            {false, "$$abc$", "", ""},

            {true, "lllll bbb xxx $dhaksdhk\n asd ${b}", "$dhaksdhk\n asd $", "dhaksdhk\n asd "},
            {false, "lllll bbb xxx $dhaksdhk\n asd ${b}", "", ""},

            {true, "aaa yyy $$bb $ dddd$", "$$bb $", "$bb "},
            {false, "aaa yyy $$bb $ dddd$", "", ""},

            {true, "aaa $sagdkg$$ Hello", "$sagdkg$", "sagdkg"},
            {false, "aaa $sagdkg$$ Hello", "", ""},

            {true, "lllll bbb xxx dhaksdhk\n asd ${b}", "", ""},
            {false, "lllll bbb xxx dhaksdhk\n asd ${b}", "", ""},

            {true, "asdhfalkhasl suaflhslia lasdjikaih sahk", "", ""},
            {false, "asdhfalkhasl suaflhslia lasdjikaih sahk", "", ""},

            {true, "lllll bbb $xxx dhaksdhk\n asd", "", ""},
            {false, "lllll bbb $xxx dhaksdhk\n asd", "", ""}
        };
    }

    @Parameterized.Parameter(0)
    public boolean allowSingle$;

    @Parameterized.Parameter(1)
    public String input;

    @Parameterized.Parameter(2)
    public String output;

    @Parameterized.Parameter(3)
    public String trimOutput;

    @Test
    public void match() {
        JLatexMathInlineProcessor processor = new JLatexMathInlineProcessor(allowSingle$);
        Matcher matcher = processor.pattern.matcher(input);
        if (output == null || "".equals(output)) {
            Assert.assertFalse(matcher.find());
        } else {
            Assert.assertTrue(matcher.find());
            String actual = matcher.group();
            Assert.assertEquals(output, actual);
            Assert.assertEquals(trimOutput, processor.trimDollar(actual));
        }
    }

    @Test
    public void not_match() {
        String[][] testCase = new String[][]{
            new String[]{"lllll bbb xxx dhaksdhk\n asd ${b}"},
            new String[]{"asdhfalkhasl suaflhslia lasdjikaih sahk"},
            new String[]{"lllll bbb $xxx dhaksdhk\n asd"},
        };
        for (String[] c : testCase) {
            String input = c[0];
            JLatexMathInlineProcessor processor = new JLatexMathInlineProcessor(true);
            Matcher matcher = processor.pattern.matcher(input);
            Assert.assertFalse(matcher.find());
        }
    }
}
