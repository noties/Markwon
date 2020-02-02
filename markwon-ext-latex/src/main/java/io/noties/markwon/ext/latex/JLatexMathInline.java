package io.noties.markwon.ext.latex;

import org.commonmark.node.CustomNode;

/**
 * @since 4.2.1-SNAPSHOT
 */
public class JLatexMathInline extends CustomNode {

    private String latex;

    public String latex() {
        return latex;
    }

    public void latex(String latex) {
        this.latex = latex;
    }
}
