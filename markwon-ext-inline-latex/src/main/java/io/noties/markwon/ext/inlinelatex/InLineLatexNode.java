package io.noties.markwon.ext.inlinelatex;

import androidx.annotation.NonNull;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;

@SuppressWarnings("WeakerAccess")
public class InLineLatexNode extends CustomNode {

    private String latex;

    public String latex() {
        return latex;
    }

    public void latex(String latex) {
        this.latex = latex;
    }
}
