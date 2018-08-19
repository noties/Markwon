package ru.noties.markwon.test;

import android.support.annotation.NonNull;

public abstract class TestUtils {

    public interface Action<T> {
        void apply(@NonNull T t);
    }

    public static <T> void with(@NonNull T t, @NonNull Action<T> action) {
        action.apply(t);
    }

    private TestUtils() {
    }
}
