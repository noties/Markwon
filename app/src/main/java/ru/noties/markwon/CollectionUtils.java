package ru.noties.markwon;

import java.util.Collection;

public abstract class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    private CollectionUtils() {}
}
