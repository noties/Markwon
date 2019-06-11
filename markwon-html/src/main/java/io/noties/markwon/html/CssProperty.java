package io.noties.markwon.html;

import androidx.annotation.NonNull;

public class CssProperty {

    private String key;
    private String value;

    CssProperty() {
    }

    void set(@NonNull String key, @NonNull String value) {
        this.key = key;
        this.value = value;
    }

    @NonNull
    public String key() {
        return key;
    }

    @NonNull
    public String value() {
        return value;
    }

    @NonNull
    public CssProperty mutate() {
        final CssProperty cssProperty = new CssProperty();
        cssProperty.set(this.key, this.value);
        return cssProperty;
    }

    @Override
    public String toString() {
        return "CssProperty{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
