package ru.noties.markwon.syntax;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.BackgroundColorSpan;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.spans.EmphasisSpan;
import ru.noties.markwon.spans.StrongEmphasisSpan;

public class Prism4jThemeDefault extends Prism4jThemeBase {

    @NonNull
    public static Prism4jThemeDefault create() {
        return new Prism4jThemeDefault();
    }

    @NonNull
    @Override
    protected ColorHashMap init() {
        return new ColorHashMap()
                .add(0xFF708090, "comment", "prolog", "doctype", "cdata")
                .add(0xFF999999, "punctuation")
                .add(0xFF990055, "property", "tag", "boolean", "number", "constant", "symbol", "deleted")
                .add(0xFF669900, "selector", "attr-name", "string", "char", "builtin", "inserted")
                .add(0xFF9a6e3a, "operator", "entity", "url")
                .add(0xFF0077aa, "atrule", "attr-value", "keyword")
                .add(0xFFDD4A68, "function", "class-name")
                .add(0xFFee9900, "regex", "important", "variable");
    }

    @Override
    protected void applyColor(
            @NonNull String language,
            @NonNull String type,
            @Nullable String alias,
            @ColorInt int color,
            @NonNull SpannableBuilder builder,
            int start,
            int end) {

        if ("css".equals(language) && isOfType("string", type, alias)) {
            super.applyColor(language, type, alias, 0xFF9a6e3a, builder, start, end);
            builder.setSpan(new BackgroundColorSpan(0x80ffffff), start, end);
            return;
        }

        if (isOfType("namespace", type, alias)) {
            color = applyAlpha(.7F, color);
        }

        super.applyColor(language, type, alias, color, builder, start, end);

        if (isOfType("important", type, alias)
                || isOfType("bold", type, alias)) {
            builder.setSpan(new StrongEmphasisSpan(), start, end);
        }

        if (isOfType("italic", type, alias)) {
            builder.setSpan(new EmphasisSpan(), start, end);
        }
    }
}
