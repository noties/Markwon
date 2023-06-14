package io.noties.markwon.app.samples.latex;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.app.samples.latex.shared.LatexHolder;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200701085820",
  title = "LaTeX inline",
  description = "Display LaTeX inline",
  artifacts = {MarkwonArtifact.EXT_LATEX, MarkwonArtifact.INLINE_PARSER},
  tags = Tag.rendering
)
public class LatexInlineSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "Here are some common mathematical formulas:\n" +
      "\n" +
      "Quadratic formula: $$ ax^2+bx+c=0 $$\n" +
      "\n" +
      "Pythagorean theorem: $$ a^2+b^2=c^2 $$\n" +
      "\n" +
      "Euler's formula: $$e^{ix}=\\cos{x}+i\\sin{x}$$\n" +
      "\n" +
      "Trigonometric identity: $$\\sin^2{x}+\\cos^2{x}=1$$\n" +
      "\n" +
      "Taylor series expansion: $$f(x)=\\sum_{n=0}^{\\infty} \\frac{f^{(n)}(a)}{n!}(x-a)^n$$\n" +
      "\n" +
      "Matrix multiplication: $$C_{i,j}=\\sum_{k=1}^{n}A_{i,k}B_{k,j}$$\n" +
      "\n" +
      "Riemann hypothesis: $$\\zeta(s)=\\sum_{n=1}^{\\infty} \\frac{1}{n^s}=\\frac{1}{1-p^{-s}}\\prod_{\\text{prime }p} \\frac{1}{1-p^{-s}}$$\n" +
      "\n" +
      "Euler's identity: $$e^{i\\pi}+1=0$$\n" +
      "\n" +
      "Fermat's Last Theorem: $$a^n+b^n=c^n$$ has no integer solutions when $$n>2$$\n" +
      "\n" +
      "Riemann hypothesis: $$\\zeta(s)=\\sum_{n=1}^\\infty\\frac{1}{n^s}$$ has all its zeros on the line $$s=\\frac{1}{2}$$ when $$s=\\frac{1}{2}+it$$\n" +
      "\n" +
      "Einstein field equations: $$G_{\\mu\\nu}=8\\pi T_{\\mu\\nu}$$\n" +
      "\n" +
      "Black-Scholes theorem: Any directed graph can be decomposed into strongly connected components\n" +
      "\n" +
      "P vs. NP conjecture by American mathematician Andrew Wiles: NP problems cannot be solved in polynomial time\n" +
      "\n" +
      "Stirling's formula: $$n!=\\sqrt{2\\pi n}\\left(\\frac{n}{e}\\right)^n$$\n" +
      "\n" +
      "Mobius inversion formula: $$f(n)=\\sum_{d|n}g(d)\\Leftrightarrow g(n)=\\sum_{d|n}\\mu(d)f\\left(\\frac{n}{d}\\right)$$\n" +
      "\n" +
      "Fourier series: $$f(x)=\\frac{a_0}{2}+\\sum_{n=1}^\\infty\\left(a_n\\cos\\frac{n\\pi x}{L}+b_n\\sin\\frac{n\\pi x}{L}\\right)$$\n" +
      "\n" +
      "Riemann integral: $$\\int_0^\\infty\\frac{x^{s-1}}{e^x-1}dx=\\Gamma(s)\\zeta(s)$$";

    // inlines must be explicitly enabled and require `MarkwonInlineParserPlugin`
    final Markwon markwon = Markwon.builder(context)
      .usePlugin(MarkwonInlineParserPlugin.create())
      .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), builder -> {
        builder.inlinesEnabled(true);
      }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
