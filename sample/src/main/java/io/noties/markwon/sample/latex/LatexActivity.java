package io.noties.markwon.sample.latex;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.ext.latex.JLatexMathTheme;
import io.noties.markwon.sample.ActivityWithMenuOptions;
import io.noties.markwon.sample.MenuOptions;
import io.noties.markwon.sample.R;

public class LatexActivity extends ActivityWithMenuOptions {

    private TextView textView;

    @NonNull
    @Override
    public MenuOptions menuOptions() {
        return MenuOptions.create()
                .add("array", this::array)
                .add("longDivision", this::longDivision)
                .add("bangle", this::bangle)
                .add("boxes", this::boxes)
                .add("insideBlockQuote", this::insideBlockQuote);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        textView = findViewById(R.id.text_view);

//        array();
        longDivision();
    }

    private void array() {
        String latex = "\\begin{array}{l}";
        latex += "\\forall\\varepsilon\\in\\mathbb{R}_+^*\\ \\exists\\eta>0\\ |x-x_0|\\leq\\eta\\Longrightarrow|f(x)-f(x_0)|\\leq\\varepsilon\\\\";
        latex += "\\det\\begin{bmatrix}a_{11}&a_{12}&\\cdots&a_{1n}\\\\a_{21}&\\ddots&&\\vdots\\\\\\vdots&&\\ddots&\\vdots\\\\a_{n1}&\\cdots&\\cdots&a_{nn}\\end{bmatrix}\\overset{\\mathrm{def}}{=}\\sum_{\\sigma\\in\\mathfrak{S}_n}\\varepsilon(\\sigma)\\prod_{k=1}^n a_{k\\sigma(k)}\\\\";
        latex += "\\sideset{_\\alpha^\\beta}{_\\gamma^\\delta}{\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}}\\\\";
        latex += "\\int_0^\\infty{x^{2n} e^{-a x^2}\\,dx} = \\frac{2n-1}{2a} \\int_0^\\infty{x^{2(n-1)} e^{-a x^2}\\,dx} = \\frac{(2n-1)!!}{2^{n+1}} \\sqrt{\\frac{\\pi}{a^{2n+1}}}\\\\";
        latex += "\\int_a^b{f(x)\\,dx} = (b - a) \\sum\\limits_{n = 1}^\\infty  {\\sum\\limits_{m = 1}^{2^n  - 1} {\\left( { - 1} \\right)^{m + 1} } } 2^{ - n} f(a + m\\left( {b - a} \\right)2^{-n} )\\\\";
        latex += "\\int_{-\\pi}^{\\pi} \\sin(\\alpha x) \\sin^n(\\beta x) dx = \\textstyle{\\left \\{ \\begin{array}{cc} (-1)^{(n+1)/2} (-1)^m \\frac{2 \\pi}{2^n} \\binom{n}{m} & n \\mbox{ odd},\\ \\alpha = \\beta (2m-n) \\\\ 0 & \\mbox{otherwise} \\\\ \\end{array} \\right .}\\\\";
        latex += "L = \\int_a^b \\sqrt{ \\left|\\sum_{i,j=1}^ng_{ij}(\\gamma(t))\\left(\\frac{d}{dt}x^i\\circ\\gamma(t)\\right)\\left(\\frac{d}{dt}x^j\\circ\\gamma(t)\\right)\\right|}\\,dt\\\\";
        latex += "\\begin{array}{rl} s &= \\int_a^b\\left\\|\\frac{d}{dt}\\vec{r}\\,(u(t),v(t))\\right\\|\\,dt \\\\ &= \\int_a^b \\sqrt{u'(t)^2\\,\\vec{r}_u\\cdot\\vec{r}_u + 2u'(t)v'(t)\\, \\vec{r}_u\\cdot\\vec{r}_v+ v'(t)^2\\,\\vec{r}_v\\cdot\\vec{r}_v}\\,\\,\\, dt. \\end{array}\\\\";
        latex += "\\end{array}";

        render(wrapLatexInSampleMarkdown(latex));
    }

    private void longDivision() {
        String latex = "\\text{A long division \\longdiv{12345}{13}";
        render(wrapLatexInSampleMarkdown(latex));
    }

    private void bangle() {
        String latex = "{a \\bangle b} {c \\brace d} {e \\brack f} {g \\choose h}";
        render(wrapLatexInSampleMarkdown(latex));
    }

    private void boxes() {
        String latex = "\\begin{array}{cc}";
        latex += "\\fbox{\\text{A framed box with \\textdbend}}&\\shadowbox{\\text{A shadowed box}}\\cr";
        latex += "\\doublebox{\\text{A double framed box}}&\\ovalbox{\\text{An oval framed box}}\\cr";
        latex += "\\end{array}";
        render(wrapLatexInSampleMarkdown(latex));
    }

    private void insideBlockQuote() {
        String latex = "W=W_1+W_2=F_1X_1-F_2X_2";
        final String md = "" +
                "# LaTeX inside a blockquote\n" +
                "> $$" + latex + "$$\n";
        render(md);
    }

    @NonNull
    private static String wrapLatexInSampleMarkdown(@NonNull String latex) {
        return "" +
                "# Example of LaTeX\n\n" +
                "(inline): $$" + latex + "$$ so nice, really-really really-really really-really? Now, (block):\n\n" +
                "$$\n" +
                "" + latex + "\n" +
                "$$\n\n" +
                "the end";
    }

    private void render(@NonNull String markdown) {

        final float textSize = textView.getTextSize();
        final Resources r = getResources();

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(JLatexMathPlugin.create(textSize, textSize * 1.25F, builder -> {
                    builder.theme()
                            .inlineBackgroundProvider(() -> new ColorDrawable(0x1000ff00))
                            .blockBackgroundProvider(() -> new ColorDrawable(0x10ff0000))
                            .blockPadding(JLatexMathTheme.Padding.symmetric(
                                    r.getDimensionPixelSize(R.dimen.latex_block_padding_vertical),
                                    r.getDimensionPixelSize(R.dimen.latex_block_padding_horizontal)
                            ));

                    // explicitly request LEGACY rendering mode
//                    builder.renderMode(JLatexMathPlugin.RenderMode.LEGACY);
                }))
                .build();

        markwon.setMarkdown(textView, markdown);
    }
}
