package io.noties.markwon.inlineparser;

import org.commonmark.internal.util.Parsing;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Node;

import java.util.regex.Pattern;

/**
 * Parses inline HTML tags
 *
 * @since 4.2.0
 */
public class HtmlInlineProcessor extends InlineProcessor {

    private static final String HTMLCOMMENT = "<!---->|<!--(?:-?[^>-])(?:-?[^-])*-->";
    private static final String PROCESSINGINSTRUCTION = "[<][?].*?[?][>]";
    private static final String DECLARATION = "<![A-Z]+\\s+[^>]*>";
    private static final String CDATA = "<!\\[CDATA\\[[\\s\\S]*?\\]\\]>";
    private static final String HTMLTAG = "(?:" + Parsing.OPENTAG + "|" + Parsing.CLOSETAG + "|" + HTMLCOMMENT
            + "|" + PROCESSINGINSTRUCTION + "|" + DECLARATION + "|" + CDATA + ")";
    private static final Pattern HTML_TAG = Pattern.compile('^' + HTMLTAG, Pattern.CASE_INSENSITIVE);

    @Override
    public char specialCharacter() {
        return '<';
    }

    @Override
    protected Node parse() {
        String m = match(HTML_TAG);
        if (m != null) {
            HtmlInline node = new HtmlInline();
            node.setLiteral(m);
            return node;
        } else {
            return null;
        }
    }
}
