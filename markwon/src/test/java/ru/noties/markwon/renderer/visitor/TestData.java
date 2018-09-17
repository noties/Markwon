package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

class TestData {

    private final String description;
    private final String input;
    private final TestConfig config;
    private final List<TestNode> output;

    TestData(
            @Nullable String description,
            @NonNull String input,
            @NonNull TestConfig config,
            @NonNull List<TestNode> output) {
        this.description = description;
        this.input = input;
        this.config = config;
        this.output = output;
    }

    @Nullable
    public String description() {
        return description;
    }

    @NonNull
    public String input() {
        return input;
    }

    @NonNull
    public TestConfig config() {
        return config;
    }

    @NonNull
    public List<TestNode> output() {
        return output;
    }
}
