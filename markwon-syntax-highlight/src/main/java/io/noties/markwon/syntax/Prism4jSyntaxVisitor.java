package io.noties.markwon.syntax;

import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;

import io.noties.prism4j.AbsVisitor;
import io.noties.prism4j.Prism4j;

class Prism4jSyntaxVisitor extends AbsVisitor {

    private final String language;
    private final Prism4jTheme theme;
    private final SpannableStringBuilder builder;

    Prism4jSyntaxVisitor(
            @NonNull String language,
            @NonNull Prism4jTheme theme,
            @NonNull SpannableStringBuilder builder) {
        this.language = language;
        this.theme = theme;
        this.builder = builder;
    }

    @Override
    protected void visitText(@NonNull Prism4j.Text text) {
        builder.append(text.literal());
    }

    @Override
    protected void visitSyntax(@NonNull Prism4j.Syntax syntax) {

        final int start = builder.length();
        visit(syntax.children());
        final int end = builder.length();

        if (end != start) {
            theme.apply(language, syntax, builder, start, end);
        }
    }
}
