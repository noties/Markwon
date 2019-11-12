package io.noties.markwon.sample.editor.inline;

import androidx.annotation.NonNull;

import org.commonmark.node.Link;
import org.commonmark.node.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public class AutolinkInline extends Inline {

    private static final Pattern EMAIL_AUTOLINK = Pattern
            .compile("^<([a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*)>");

    private static final Pattern AUTOLINK = Pattern
            .compile("^<[a-zA-Z][a-zA-Z0-9.+-]{1,31}:[^<>\u0000-\u0020]*>");

    @NonNull
    @Override
    public Collection<Character> characters() {
        return Collections.singleton('<');
    }

    @Override
    public boolean parse() {
        String m;
        if ((m = match(EMAIL_AUTOLINK)) != null) {
            String dest = m.substring(1, m.length() - 1);
            Link node = new Link("mailto:" + dest, null);
            node.appendChild(new Text(dest));
            appendNode(node);
            return true;
        } else if ((m = match(AUTOLINK)) != null) {
            String dest = m.substring(1, m.length() - 1);
            Link node = new Link(dest, null);
            node.appendChild(new Text(dest));
            appendNode(node);
            return true;
        } else {
            return false;
        }
    }
}
