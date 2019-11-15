package io.noties.markwon.inlineparser;

import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

import java.util.regex.Pattern;

/**
 * Parses autolinks, for example {@code <me@mydoma.in>}
 *
 * @since 4.2.0
 */
public class AutolinkInlineProcessor extends InlineProcessor {

    private static final Pattern EMAIL_AUTOLINK = Pattern
            .compile("^<([a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*)>");

    private static final Pattern AUTOLINK = Pattern
            .compile("^<[a-zA-Z][a-zA-Z0-9.+-]{1,31}:[^<>\u0000-\u0020]*>");

    @Override
    public char specialCharacter() {
        return '<';
    }

    @Override
    protected Node parse() {
        String m;
        if ((m = match(EMAIL_AUTOLINK)) != null) {
            String dest = m.substring(1, m.length() - 1);
            Link node = new Link("mailto:" + dest, null);
            node.appendChild(new Text(dest));
            return node;
        } else if ((m = match(AUTOLINK)) != null) {
            String dest = m.substring(1, m.length() - 1);
            Link node = new Link(dest, null);
            node.appendChild(new Text(dest));
            return node;
        } else {
            return null;
        }
    }
}
