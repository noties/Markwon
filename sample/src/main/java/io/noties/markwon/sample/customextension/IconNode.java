package io.noties.markwon.sample.customextension;

import android.support.annotation.NonNull;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;

@SuppressWarnings("WeakerAccess")
public class IconNode extends CustomNode implements Delimited {

    public static final char DELIMITER = '@';

    public static final String DELIMITER_STRING = "" + DELIMITER;


    private final String name;

    private final String color;

    private final String size;

    public IconNode(@NonNull String name, @NonNull String color, @NonNull String size) {
        this.name = name;
        this.color = color;
        this.size = size;
    }

    @NonNull
    public String name() {
        return name;
    }

    @NonNull
    public String color() {
        return color;
    }

    @NonNull
    public String size() {
        return size;
    }

    @Override
    public String getOpeningDelimiter() {
        return DELIMITER_STRING;
    }

    @Override
    public String getClosingDelimiter() {
        return DELIMITER_STRING;
    }

    @Override
    public String toString() {
        return "IconNode{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
