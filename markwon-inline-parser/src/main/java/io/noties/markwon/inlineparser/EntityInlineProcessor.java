package io.noties.markwon.inlineparser;

import org.commonmark.internal.util.Html5Entities;

import java.util.regex.Pattern;

/**
 * Parses HTML entities {@code &amp;}
 *
 * @since 4.2.0-SNAPSHOT
 */
public class EntityInlineProcessor extends InlineProcessor {

    private static final String ENTITY = "&(?:#x[a-f0-9]{1,8}|#[0-9]{1,8}|[a-z][a-z0-9]{1,31});";
    private static final Pattern ENTITY_HERE = Pattern.compile('^' + ENTITY, Pattern.CASE_INSENSITIVE);

    @Override
    public char specialCharacter() {
        return '&';
    }

    @Override
    protected boolean parse() {
        String m;
        if ((m = match(ENTITY_HERE)) != null) {
            appendText(Html5Entities.entityToString(m));
            return true;
        } else {
            return false;
        }
    }
}
