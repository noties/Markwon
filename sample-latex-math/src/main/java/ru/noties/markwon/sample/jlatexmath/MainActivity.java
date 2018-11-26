package ru.noties.markwon.sample.jlatexmath;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.commonmark.node.CustomBlock;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import ru.noties.jlatexmath.JLatexMathAndroid;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.il.AsyncDrawableLoader;
import ru.noties.markwon.image.ImageSize;
import ru.noties.markwon.renderer.SpannableMarkdownVisitor;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = findViewById(R.id.text_view);

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

//        String latex = "\\text{A long division \\longdiv{12345}{13}";
//                String latex = "{a \\bangle b} {c \\brace d} {e \\brack f} {g \\choose h}";

//        String latex = "\\begin{array}{cc}";
//        latex += "\\fbox{\\text{A framed box with \\textdbend}}&\\shadowbox{\\text{A shadowed box}}\\cr";
//        latex += "\\doublebox{\\text{A double framed box}}&\\ovalbox{\\text{An oval framed box}}\\cr";
//        latex += "\\end{array}";


        final JLatexMathMedia.Config config = new JLatexMathMedia.Config(textView.getTextSize()) {{
//            align = JLatexMathDrawable.ALIGN_RIGHT;
        }};
        final JLatexMathMedia jLatexMathMedia = new JLatexMathMedia(config);

        final AsyncDrawableLoader asyncDrawableLoader = AsyncDrawableLoader.builder()
                .addSchemeHandler(jLatexMathMedia.schemeHandler())
                .mediaDecoders(jLatexMathMedia.mediaDecoder())
                .build();

        final MarkwonConfiguration configuration = MarkwonConfiguration.builder(this)
                .asyncDrawableLoader(asyncDrawableLoader)
                .build();

        final String markdown = "# Example of LaTeX\n\n$$"
                + latex + "$$\n\n something like **this**";

        final Parser parser = new Parser.Builder()
                .customBlockParserFactory(new JLatexMathBlockParser.Factory())
                .build();

        final Node node = parser.parse(markdown);
        final SpannableBuilder builder = new SpannableBuilder();
        final SpannableMarkdownVisitor visitor = new SpannableMarkdownVisitor(MarkwonConfiguration.create(this), builder) {

            @Override
            public void visit(CustomBlock customBlock) {

                if (!(customBlock instanceof JLatexMathBlock)) {
                    super.visit(customBlock);
                    return;
                }

                final String latex = ((JLatexMathBlock) customBlock).latex();

                final int length = builder.length();
                builder.append(latex);

                SpannableBuilder.setSpans(
                        builder,
                        configuration.factory().image(
                                configuration.theme(),
                                JLatexMathMedia.makeDestination(latex),
                                configuration.asyncDrawableLoader(),
                                configuration.imageSizeResolver(),
                                new ImageSize(new ImageSize.Dimension(100, "%"), null),
                                false
                        ),
                        length,
                        builder.length()
                );
            }
        };
        node.accept(visitor);

        Markwon.setText(textView, builder.text());
    }
}
