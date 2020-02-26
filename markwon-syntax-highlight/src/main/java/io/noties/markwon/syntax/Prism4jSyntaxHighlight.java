package io.noties.markwon.syntax;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.prism4j.Prism4j;

public class Prism4jSyntaxHighlight implements SyntaxHighlight {

    @NonNull
    public static Prism4jSyntaxHighlight create(
            @NonNull Prism4j prism4j,
            @NonNull Prism4jTheme theme) {
        return new Prism4jSyntaxHighlight(prism4j, theme, null);
    }

    @NonNull
    public static Prism4jSyntaxHighlight create(
            @NonNull Prism4j prism4j,
            @NonNull Prism4jTheme theme,
            @Nullable String fallback) {
        return new Prism4jSyntaxHighlight(prism4j, theme, fallback);
    }

    private final Prism4j prism4j;
    private final Prism4jTheme theme;
    private final String fallback;

    protected Prism4jSyntaxHighlight(
            @NonNull Prism4j prism4j,
            @NonNull Prism4jTheme theme,
            @Nullable String fallback) {
        this.prism4j = prism4j;
        this.theme = theme;
        this.fallback = fallback;
    }

    @NonNull
    @Override
    public CharSequence highlight(@Nullable String info, @NonNull String code) {

        // @since 4.2.2
        // although not null, but still is empty
        if (code.isEmpty()) {
            return code;
        }

        // if info is null, do not highlight -> LICENCE footer very commonly wrapped inside code
        // block without syntax name specified (so, do not highlight)
        return info == null
                ? highlightNoLanguageInfo(code)
                : highlightWithLanguageInfo(info, code);
    }

    @NonNull
    protected CharSequence highlightNoLanguageInfo(@NonNull String code) {
        return code;
    }

    @NonNull
    protected CharSequence highlightWithLanguageInfo(@NonNull String info, @NonNull String code) {

        final CharSequence out;

        final String language;
        final Prism4j.Grammar grammar;
        {
            String _language = info;
            Prism4j.Grammar _grammar = prism4j.grammar(info);
            if (_grammar == null && !TextUtils.isEmpty(fallback)) {
                _language = fallback;
                _grammar = prism4j.grammar(fallback);
            }
            language = _language;
            grammar = _grammar;
        }

        if (grammar != null) {
            out = highlight(language, grammar, code);
        } else {
            out = code;
        }

        return out;
    }

    @NonNull
    protected CharSequence highlight(@NonNull String language, @NonNull Prism4j.Grammar grammar, @NonNull String code) {
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        final Prism4jSyntaxVisitor visitor = new Prism4jSyntaxVisitor(language, theme, builder);
        visitor.visit(prism4j.tokenize(code, grammar));
        return builder;
    }

    @NonNull
    protected Prism4j prism4j() {
        return prism4j;
    }

    @NonNull
    protected Prism4jTheme theme() {
        return theme;
    }

    @Nullable
    protected String fallback() {
        return fallback;
    }
}
