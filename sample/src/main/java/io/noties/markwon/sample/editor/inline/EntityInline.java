package io.noties.markwon.sample.editor.inline;

import androidx.annotation.NonNull;

import org.commonmark.internal.util.Html5Entities;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public class EntityInline extends Inline {

    private static final String ENTITY = "&(?:#x[a-f0-9]{1,8}|#[0-9]{1,8}|[a-z][a-z0-9]{1,31});";
    private static final Pattern ENTITY_HERE = Pattern.compile('^' + ENTITY, Pattern.CASE_INSENSITIVE);

    @NonNull
    @Override
    public Collection<Character> characters() {
        return Collections.singleton('&');
    }

    @Override
    public boolean parse() {
        String m;
        if ((m = match(ENTITY_HERE)) != null) {
            appendText(Html5Entities.entityToString(m));
            return true;
        } else {
            return false;
        }
    }
}
