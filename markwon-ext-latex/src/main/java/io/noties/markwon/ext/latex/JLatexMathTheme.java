package io.noties.markwon.ext.latex;

import android.graphics.Rect;

/**
 * @since 4.3.0-SNAPSHOT
 */
public class JLatexMathTheme {

    private float textSize;
    private float inlineTextSize;
    private float blockTextSize;

    // TODO: move to a class
    private JLatexMathPlugin.BackgroundProvider backgroundProvider;
    private JLatexMathPlugin.BackgroundProvider inlineBackgroundProvider;
    private JLatexMathPlugin.BackgroundProvider blockBackgroundProvider;

    private boolean blockFitCanvas;
    // horizontal alignment (when there is additional horizontal space)
    private int blockAlign;

    private Rect padding;
    private Rect inlinePadding;
    private Rect blockPadding;


}
