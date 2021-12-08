package io.noties.markwon.ext.inlinelatex;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

import java.util.regex.Pattern;

import io.noties.markwon.inlineparser.InlineProcessor;

public class InLineLatexProcessor extends InlineProcessor {

    @NonNull
    public static InLineLatexProcessor create() {
        return new InLineLatexProcessor();
    }


    @NonNull
    public static String prepare(@NonNull String input) {
        final StringBuilder builder = new StringBuilder(input);
        return builder.toString();
    }

    private static final Pattern RE = Pattern.compile("(\\${2})([\\s\\S]+?)\\1");

    @Override
    public char specialCharacter() {
        return '$';
    }

    @Nullable
    @Override
    protected Node parse() {

        final String latex = match(RE);
        if (latex == null) {
            return null;
        }

        final InLineLatexNode node = new InLineLatexNode();
        node.latex(latex.substring(2, latex.length() - 2));
        return node;
    }
}
