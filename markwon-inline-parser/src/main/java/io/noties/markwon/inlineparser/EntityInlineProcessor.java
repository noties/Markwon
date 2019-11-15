package io.noties.markwon.inlineparser;

import org.commonmark.internal.util.Escaping;
import org.commonmark.internal.util.Html5Entities;
import org.commonmark.node.Node;

import java.util.regex.Pattern;

/**
 * Parses HTML entities {@code &amp;}
 *
 * @since 4.2.0
 */
public class EntityInlineProcessor extends InlineProcessor {

    private static final Pattern ENTITY_HERE = Pattern.compile('^' + Escaping.ENTITY, Pattern.CASE_INSENSITIVE);

    @Override
    public char specialCharacter() {
        return '&';
    }

    @Override
    protected Node parse() {
        String m;
        if ((m = match(ENTITY_HERE)) != null) {
            return text(Html5Entities.entityToString(m));
        } else {
            return null;
        }
    }
}
