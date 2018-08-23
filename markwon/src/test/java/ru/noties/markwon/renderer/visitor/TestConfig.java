package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;

import java.util.Map;

class TestConfig {

    static final String USE_PARAGRAPHS = "use-paragraphs";
    static final String USE_HTML = "use-html";
    static final String SOFT_BREAK_ADDS_NEW_LINE = "soft-break-adds-new-line";
    static final String HTML_ALLOW_NON_CLOSED_TAGS = "html-allow-non-closed-tags";

    private final Map<String, Boolean> map;

    TestConfig(@NonNull Map<String, Boolean> map) {
        this.map = map;
    }

    boolean hasOption(@NonNull String option) {
        final Boolean value = map.get(option);
        return value != null && value;
    }

    @Override
    public String toString() {
        return "TestConfig{" +
                "map=" + map +
                '}';
    }
}
