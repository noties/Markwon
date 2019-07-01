package io.noties.markwon.html.jsoup.nodes;

import androidx.annotation.NonNull;

import org.commonmark.internal.util.Html5Entities;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

public abstract class CommonMarkEntities {

    public static boolean isNamedEntity(@NonNull String name) {
        return COMMONMARK_NAMED_ENTITIES.containsKey(name);
    }

    public static int codepointsForName(@NonNull String name, @NonNull int[] codepoints) {
        final String value = COMMONMARK_NAMED_ENTITIES.get(name);
        if (value != null) {
            final int length = value.length();
            if (length == 1) {
                codepoints[0] = value.charAt(0);
            } else {
                codepoints[0] = value.charAt(0);
                codepoints[1] = value.charAt(1);
            }
            return length;
        }
        return 0;
    }

    private static final Map<String, String> COMMONMARK_NAMED_ENTITIES;

    static {
        Map<String, String> map;
        try {
            final Field field = Html5Entities.class.getDeclaredField("NAMED_CHARACTER_REFERENCES");
            field.setAccessible(true);
            //noinspection unchecked
            map = (Map<String, String>) field.get(null);
        } catch (Throwable t) {
            map = Collections.emptyMap();
            t.printStackTrace();
        }
        COMMONMARK_NAMED_ENTITIES = map;
    }

    private CommonMarkEntities() {
    }
}
