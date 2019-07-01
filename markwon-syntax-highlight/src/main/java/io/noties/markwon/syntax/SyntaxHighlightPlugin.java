package io.noties.markwon.syntax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.prism4j.Prism4j;

public class SyntaxHighlightPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static SyntaxHighlightPlugin create(
            @NonNull Prism4j prism4j,
            @NonNull Prism4jTheme theme) {
        return create(prism4j, theme, null);
    }

    @NonNull
    public static SyntaxHighlightPlugin create(
            @NonNull Prism4j prism4j,
            @NonNull Prism4jTheme theme,
            @Nullable String fallbackLanguage) {
        return new SyntaxHighlightPlugin(prism4j, theme, fallbackLanguage);
    }

    private final Prism4j prism4j;
    private final Prism4jTheme theme;
    private final String fallbackLanguage;

    public SyntaxHighlightPlugin(
            @NonNull Prism4j prism4j,
            @NonNull Prism4jTheme theme,
            @Nullable String fallbackLanguage) {
        this.prism4j = prism4j;
        this.theme = theme;
        this.fallbackLanguage = fallbackLanguage;
    }

    @Override
    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
        builder
                .codeTextColor(theme.textColor())
                .codeBackgroundColor(theme.background());
    }

    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
        builder.syntaxHighlight(Prism4jSyntaxHighlight.create(prism4j, theme, fallbackLanguage));
    }
}
