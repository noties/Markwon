package io.noties.markwon.sample.latex;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import io.noties.debug.Debug;
import io.noties.markwon.Markwon;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.ext.latex.JLatexMathTheme;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.sample.ActivityWithMenuOptions;
import io.noties.markwon.sample.MenuOptions;
import io.noties.markwon.sample.R;

public class LatexActivity extends ActivityWithMenuOptions {

    private static final String LATEX_ARRAY;

    static {
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
        LATEX_ARRAY = latex;
    }

    private static final String LATEX_LONG_DIVISION = "\\text{A long division \\longdiv{12345}{13}";
    private static final String LATEX_BANGLE = "{a \\bangle b} {c \\brace d} {e \\brack f} {g \\choose h}";
    private static final String LATEX_BOXES;

    static {
        String latex = "\\begin{array}{cc}";
        latex += "\\fbox{\\text{A framed box with \\textdbend}}&\\shadowbox{\\text{A shadowed box}}\\cr";
        latex += "\\doublebox{\\text{A double framed box}}&\\ovalbox{\\text{An oval framed box}}\\cr";
        latex += "\\end{array}";
        LATEX_BOXES = latex;
    }

    private TextView textView;
    private View parent;

    @NonNull
    @Override
    public MenuOptions menuOptions() {
        return MenuOptions.create()
                .add("array", this::array)
                .add("longDivision", this::longDivision)
                .add("bangle", this::bangle)
                .add("boxes", this::boxes)
                .add("insideBlockQuote", this::insideBlockQuote)
                .add("error", this::error)
                .add("legacy", this::legacy)
                .add("textColor", this::textColor)
                .add("defaultTextColor", this::defaultTextColor)
                .add("inlineAndBlock", this::inlineAndBlock)
                .add("dark", this::dark);
    }

    @Override
    protected void beforeOptionSelected(@NonNull String option) {
        super.beforeOptionSelected(option);

        // reset text color
        textView.setTextColor(0xFF000000);

        // reset background
        parent.setBackgroundColor(0xFFffffff);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        textView = findViewById(R.id.text_view);
        parent = findViewById(R.id.scroll_view);

//        array();
        longDivision();
    }

    private void array() {
        renderWithBlocksAndInlines(wrapLatexInSampleMarkdown(LATEX_ARRAY));
    }

    private void longDivision() {
        renderWithBlocksAndInlines(wrapLatexInSampleMarkdown(LATEX_LONG_DIVISION));
    }

    private void bangle() {
        renderWithBlocksAndInlines(wrapLatexInSampleMarkdown(LATEX_BANGLE));
    }

    private void boxes() {
        renderWithBlocksAndInlines(wrapLatexInSampleMarkdown(LATEX_BOXES));
    }

    private void insideBlockQuote() {
        String latex = "W=W_1+W_2=F_1X_1-F_2X_2";
        final String md = "" +
                "# LaTeX inside a blockquote\n" +
                "> $$" + latex + "$$\n";
        renderWithBlocksAndInlines(md);
    }

    private void error() {
        final String md = wrapLatexInSampleMarkdown("\\sum_{i=0}^\\infty x \\cdot 0 \\rightarrow \\iMightNotExist{0}");

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), builder -> {
                    builder.inlinesEnabled(true);
                    //noinspection Convert2Lambda
                    builder.errorHandler(new JLatexMathPlugin.ErrorHandler() {
                        @Nullable
                        @Override
                        public Drawable handleError(@Nullable String latex, @NonNull Throwable error) {
                            Debug.e(error, latex);
                            return ContextCompat.getDrawable(LatexActivity.this, R.drawable.ic_android_black_24dp);
                        }
                    });
                }))
                .build();

        markwon.setMarkdown(textView, md);
    }

    private void legacy() {
        final String md = wrapLatexInSampleMarkdown(LATEX_BANGLE);

        final Markwon markwon = Markwon.builder(this)
                // LEGACY does not require inline parser
                .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), builder -> {
                    builder.blocksLegacy(true);
                    builder.theme()
                            .backgroundProvider(() -> new ColorDrawable(0x100000ff))
                            .padding(JLatexMathTheme.Padding.all(48));
                }))
                .build();

        markwon.setMarkdown(textView, md);
    }

    private void textColor() {
        final String md = wrapLatexInSampleMarkdown(LATEX_LONG_DIVISION);
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), builder -> {
                    builder.inlinesEnabled(true);
                    builder.theme()
                            .inlineTextColor(Color.RED)
                            .blockTextColor(Color.GREEN)
                            .inlineBackgroundProvider(() -> new ColorDrawable(Color.YELLOW))
                            .blockBackgroundProvider(() -> new ColorDrawable(Color.GRAY));
                }))
                .build();
        markwon.setMarkdown(textView, md);
    }

    private void defaultTextColor() {
        // @since 4.3.0 text color is automatically taken from textView
        //  (if it's not specified explicitly via configuration)
        textView.setTextColor(0xFFff0000);

        final String md = wrapLatexInSampleMarkdown(LATEX_LONG_DIVISION);
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), new JLatexMathPlugin.BuilderConfigure() {
                    @Override
                    public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
                        builder.inlinesEnabled(true);
                        // override default text color
                        builder.theme()
                                .inlineTextColor(0xFF00ffff);
                    }
                }))
                .build();

        markwon.setMarkdown(textView, md);
    }

    private void inlineAndBlock() {
        final String md = "" +
                "# Inline and block\n\n" +
                "$$\\int_{a}^{b} f(x)dx = F(b) - F(a)$$\n\n" +
                "this was **inline** _LaTeX_ $$\\int_{a}^{b} f(x)dx = F(b) - F(a)$$ and once again it was\n\n" +
                "Now a block:\n\n" +
                "$$\n" +
                "\\int_{a}^{b} f(x)dx = F(b) - F(a)\n" +
                "$$\n\n" +
                "Not a block (content on delimited line), but inline instead:\n\n" +
                "$$\\int_{a}^{b} f(x)dx = F(b) - F(a)$$" +
                "\n\n" +
                "that's it";
        renderWithBlocksAndInlines(md);
    }

    private void dark() {
        parent.setBackgroundColor(0xFF000000);
        textView.setTextColor(0xFFffffff);

        String latex = "W=W_1+W_2=F_1X_1-F_2X_2";
        final String md = "" +
                "# LaTeX inside a blockquote\n" +
                "> $$" + latex + "$$\n";
        renderWithBlocksAndInlines(md);
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

    private void renderWithBlocksAndInlines(@NonNull String markdown) {

        final float textSize = textView.getTextSize();
        final Resources r = getResources();

        final Markwon markwon = Markwon.builder(this)
                // NB! `MarkwonInlineParserPlugin` is required in order to parse inlines
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(textSize, textSize * 1.25F, builder -> {
                    // Important thing to do is to enable inlines (by default disabled)
                    builder.inlinesEnabled(true);
                    builder.theme()
                            .inlineBackgroundProvider(() -> new ColorDrawable(0x1000ff00))
                            .blockBackgroundProvider(() -> new ColorDrawable(0x10ff0000))
                            .blockPadding(JLatexMathTheme.Padding.symmetric(
                                    r.getDimensionPixelSize(R.dimen.latex_block_padding_vertical),
                                    r.getDimensionPixelSize(R.dimen.latex_block_padding_horizontal)
                            ));
                }))
                .build();

        markwon.setMarkdown(textView, markdown);
    }
}
