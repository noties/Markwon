package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;

import java.util.Map;

public class TestEntry {

    private final String name;
    private final String text;
    private final Map<String, String> attributes;

    TestEntry(@NonNull String name, @NonNull String text, @NonNull Map<String, String> attributes) {
        this.name = name;
        this.text = text;
        this.attributes = attributes;
    }

    @NonNull
    public String name() {
        return name;
    }

    @NonNull
    public String text() {
        return text;
    }

    @NonNull
    public Map<String, String> attributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "TestEntry{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
