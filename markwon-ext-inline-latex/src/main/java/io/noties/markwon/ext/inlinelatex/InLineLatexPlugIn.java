package io.noties.markwon.ext.inlinelatex;

import android.graphics.Color;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import androidx.annotation.NonNull;
import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.List;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonVisitor;

import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.span.ext.CenteredImageSpan;
import ru.noties.jlatexmath.JLatexMathDrawable;

public class InLineLatexPlugIn extends AbstractMarkwonPlugin {
    private static float mLatextSize;
    private static int mScreenWidth;
    @NonNull
    public static InLineLatexPlugIn create(float latexSize, int screenWidth)
    {
        mLatextSize = latexSize;
        mScreenWidth = screenWidth;
        return new InLineLatexPlugIn();
    }

    @Override
    public void configure(@NonNull Registry registry) {
        registry.require(MarkwonInlineParserPlugin.class).factoryBuilder().addInlineProcessor(new InLineLatexProcessor());
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.customBlockParserFactory(new InLineLatexBlockParser.Factory());
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(InLineLatexNode.class, new MarkwonVisitor.NodeVisitor<InLineLatexNode>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull InLineLatexNode inLineLinkNode) {
                final String latex = inLineLinkNode.latex();
                int latxtColor = Color.BLACK;
                int backgroundColor = Color.TRANSPARENT;
                int errorColor = Color.parseColor("#ff3d00");
                if (!TextUtils.isEmpty(latex)) {
                    if (latex.trim().equalsIgnoreCase("{")) {
                        String errTxt = "LaTeX syntax error";
                        ForegroundColorSpan fgColorSpan = new ForegroundColorSpan(errorColor);
                        visitor.builder().append(errTxt, fgColorSpan);
                    } else {
                        try {
                            int txtLength = latex.length();
                            float textWidth = mScreenWidth;
                            final JLatexMathDrawable latexDrawable = JLatexMathDrawable.builder(replaceLatexTag(latex))
                                    .textSize(mLatextSize)
                                    .color(latxtColor)
                                    .background(backgroundColor)
                                    .fitCanvas(false) // It will fix the truncated issue of inline latex
                                    .build();
                            float latexWidth = latexDrawable.getIntrinsicWidth();
                            // Inline latex wrap
                            if (latexWidth > textWidth) {
                                float oneLetterWidth = latexWidth / txtLength;
                                if (oneLetterWidth < 22) {
                                    oneLetterWidth = 22;
                                }
                                int allowLen = Math.round(textWidth / oneLetterWidth) - 2;
                                List<String> spiltedText = splitStringByLen(latex, allowLen);
                                for (int txtIndex = 0; txtIndex < spiltedText.size(); txtIndex ++) {
                                    String subText = spiltedText.get(txtIndex);
                                    int subTextLen = subText.length();
                                    final JLatexMathDrawable subLatexDrawable = JLatexMathDrawable.builder(replaceLatexTag(subText))
                                            .textSize(mLatextSize)
                                            .color(latxtColor)
                                            .background(backgroundColor)
                                            .fitCanvas(true) // It will fix the truncated issue of inline latex
                                            .build();
                                    visitor.builder().append(subText, new CenteredImageSpan(subLatexDrawable));
                                }
                            } else {
                                visitor.builder().append(latex, new CenteredImageSpan(latexDrawable));
                            }
                            visitor.builder().append(' ');
                        } catch (Exception e) {
                            String errTxt = "LaTeX syntax error";
                            ForegroundColorSpan fgColorSpan = new ForegroundColorSpan(errorColor);
                            visitor.builder().append(errTxt, fgColorSpan);
                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public String processMarkdown(@NonNull String markdown) {
        return InLineLatexProcessor.prepare(markdown);
    }

    public String replaceLatexTag(String latex) {
        String latexText = latex.replaceAll("\\\\exist ", "\\\\exists ");
        return latexText;
    }

    public List<String> splitStringByLen(String text, int length) {
        List<String> strings = new ArrayList<String>();
        int index = 0;
        while (index < text.length()) {
            String splitText = text.substring(index, Math.min(index + length,text.length()));
            int nWhistSpace = splitText.lastIndexOf(" ");
            if (nWhistSpace > 0 && splitText.length() >= length) {
                String splitWord = splitText.substring(0, nWhistSpace);
                strings.add(splitWord);
                index += nWhistSpace;
            } else  {
                strings.add(splitText);
                index += length;
            }
        }
        return strings;
    }
}
