package io.noties.markwon.syntax;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.core.spans.EmphasisSpan;
import io.noties.markwon.core.spans.StrongEmphasisSpan;

public class Prism4jThemeDarkula extends Prism4jThemeBase {

    @NonNull
    public static Prism4jThemeDarkula create() {
        return new Prism4jThemeDarkula(0xFF2d2d2d);
    }

    /**
     * @param background color
     * @since 3.0.0
     */
    @NonNull
    public static Prism4jThemeDarkula create(@ColorInt int background) {
        return new Prism4jThemeDarkula(background);
    }

    private final int background;

    public Prism4jThemeDarkula(@ColorInt int background) {
        this.background = background;
    }

    @Override
    public int background() {
        return background;
    }

    @Override
    public int textColor() {
        return 0xFFa9b7c6;
    }

    @NonNull
    @Override
    protected ColorHashMap init() {
        return new ColorHashMap()
                .add(0xFF808080, "comment", "prolog", "cdata")
                .add(0xFFcc7832, "delimiter", "boolean", "keyword", "selector", "important", "atrule")
                .add(0xFFa9b7c6, "operator", "punctuation", "attr-name")
                .add(0xFFe8bf6a, "tag", "doctype", "builtin")
                .add(0xFF6897bb, "entity", "number", "symbol")
                .add(0xFF9876aa, "property", "constant", "variable")
                .add(0xFF6a8759, "string", "char")
                .add(0xFFbbb438, "annotation")
                .add(0xFFa5c261, "attr-value")
                .add(0xFF287bde, "url")
                .add(0xFFffc66d, "function")
                .add(0xFF364135, "regex")
                .add(0xFF294436, "inserted")
                .add(0xFF484a4a, "deleted");
    }

    @Override
    protected void applyColor(@NonNull String language, @NonNull String type, @Nullable String alias, int color, @NonNull SpannableStringBuilder builder, int start, int end) {
        super.applyColor(language, type, alias, color, builder, start, end);

        if (isOfType("important", type, alias)
                || isOfType("bold", type, alias)) {
            builder.setSpan(new StrongEmphasisSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (isOfType("italic", type, alias)) {
            builder.setSpan(new EmphasisSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
