package io.noties.markwon.ext.latex;

import org.commonmark.node.Node;

import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.noties.markwon.inlineparser.InlineProcessor;

/**
 * @since 4.3.0
 */
class JLatexMathInlineProcessor extends InlineProcessor {

    private static final Pattern singleOrDoubleDollar =
        Pattern.compile("(\\${2})([\\s\\S]+?)(\\${2})|(\\$)([\\s\\S]+?)(\\$)");
    private static final Pattern doubleDollar =
        Pattern.compile("(\\${2})([\\s\\S]+?)\\1");

    private final boolean allowSingle$;

    @VisibleForTesting
    final Pattern pattern;

    JLatexMathInlineProcessor() {
        this(false);
    }

    JLatexMathInlineProcessor(boolean allowSingle$) {
        this.allowSingle$ = allowSingle$;
        this.pattern = allowSingle$ ? singleOrDoubleDollar : doubleDollar;
    }

    @Override
    public char specialCharacter() {
        return '$';
    }

    @Nullable
    @Override
    protected Node parse() {

        final String latex = match(pattern);
        if (latex == null) {
            return null;
        }

        final JLatexMathNode node = new JLatexMathNode();
        node.latex(trimDollar(latex));
        return node;
    }

    @SuppressWarnings("DuplicateExpressions")
    @VisibleForTesting
    String trimDollar(String latex) {
        if (allowSingle$) {
            return latex.startsWith("$$") && latex.endsWith("$$")
                ? latex.substring(2, latex.length() - 2)
                : latex.substring(1, latex.length() - 1);
        } else {
            return latex.substring(2, latex.length() - 2);
        }
    }
}
