package io.noties.markwon.sample.editor.inline;

import androidx.annotation.NonNull;

import org.commonmark.internal.util.Parsing;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public class HtmlInline extends Inline {

    private static final String HTMLCOMMENT = "<!---->|<!--(?:-?[^>-])(?:-?[^-])*-->";
    private static final String PROCESSINGINSTRUCTION = "[<][?].*?[?][>]";
    private static final String DECLARATION = "<![A-Z]+\\s+[^>]*>";
    private static final String CDATA = "<!\\[CDATA\\[[\\s\\S]*?\\]\\]>";
    private static final String HTMLTAG = "(?:" + Parsing.OPENTAG + "|" + Parsing.CLOSETAG + "|" + HTMLCOMMENT
            + "|" + PROCESSINGINSTRUCTION + "|" + DECLARATION + "|" + CDATA + ")";
    private static final Pattern HTML_TAG = Pattern.compile('^' + HTMLTAG, Pattern.CASE_INSENSITIVE);

    @NonNull
    @Override
    public Collection<Character> characters() {
        return Collections.singleton('<');
    }

    @Override
    public boolean parse() {
        String m = match(HTML_TAG);
        if (m != null) {
            org.commonmark.node.HtmlInline node = new org.commonmark.node.HtmlInline();
            node.setLiteral(m);
            appendNode(node);
            return true;
        } else {
            return false;
        }
    }
}
