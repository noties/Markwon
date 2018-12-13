package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Map;

class TestSpan {

    static final String STRONG_EMPHASIS = "b";
    static final String EMPHASIS = "i";
    static final String BLOCK_QUOTE = "blockquote";
    static final String CODE = "code";
    static final String CODE_BLOCK = "code-block";
    static final String ORDERED_LIST = "ol";
    static final String BULLET_LIST = "ul";
    static final String THEMATIC_BREAK = "hr";
    static final String HEADING = "h";
//    static final String STRIKE_THROUGH = "s";
//    static final String TASK_LIST = "task-list";
//    static final String TABLE_ROW = "tr";
    static final String PARAGRAPH = "p";
    static final String IMAGE = "img";
    static final String LINK = "a";
//    static final String SUPER_SCRIPT = "sup";
//    static final String SUB_SCRIPT = "sub";
//    static final String UNDERLINE = "u";


    private final String name;
    private final Map<String, String> attributes;

    TestSpan(@NonNull String name) {
        this(name, Collections.<String, String>emptyMap());
    }

    TestSpan(@NonNull String name, @NonNull Map<String, String> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    @NonNull
    public String name() {
        return name;
    }

    @NonNull
    public Map<String, String> attributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "TestSpan{" +
                "name='" + name + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
