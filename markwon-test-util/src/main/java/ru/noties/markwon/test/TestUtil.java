package ru.noties.markwon.test;

import android.support.annotation.NonNull;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class TestUtil {

    @NonNull
    public static String read(@NonNull String path) {
        try {
            return IOUtils.resourceToString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static String read(@NonNull Object who, @NonNull String path) {
        try {
            return IOUtils.resourceToString(path, StandardCharsets.UTF_8, who.getClass().getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TestUtil() {
    }
}
