package ru.noties.markwon.renderer;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;

import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.Image;
import org.commonmark.node.ListBlock;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;

import ru.noties.debug.Debug;
import ru.noties.markwon.spans.BlockQuoteSpan;
import ru.noties.markwon.spans.BulletListItemSpan;
import ru.noties.markwon.spans.CodeSpan;
import ru.noties.markwon.spans.EmphasisSpan;
import ru.noties.markwon.spans.HeadingSpan;
import ru.noties.markwon.spans.StrongEmphasisSpan;
import ru.noties.markwon.spans.ThematicBreakSpan;

public class SpannableMarkdownVisitor extends AbstractVisitor {

    // http://spec.commonmark.org/0.18/#html-blocks

    private final SpannableConfiguration configuration;
    private final SpannableStringBuilder builder;

    private int blockQuoteIndent;
    private int listLevel;

    public SpannableMarkdownVisitor(
            @NonNull SpannableConfiguration configuration,
            @NonNull SpannableStringBuilder builder
    ) {
        this.configuration = configuration;
        this.builder = builder;
    }

    @Override
    public void visit(Text text) {
        Debug.i(text);
        builder.append(text.getLiteral());
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        Debug.i(strongEmphasis);
        final int length = builder.length();
        visitChildren(strongEmphasis);
        setSpan(length, new StrongEmphasisSpan());
    }

    @Override
    public void visit(Emphasis emphasis) {
        Debug.i(emphasis);
        final int length = builder.length();
        visitChildren(emphasis);
        setSpan(length, new EmphasisSpan());
    }

    @Override
    public void visit(BlockQuote blockQuote) {

        newLine();

        final int length = builder.length();

        blockQuoteIndent += 1;

        visitChildren(blockQuote);

        setSpan(length, new BlockQuoteSpan(
                configuration.getBlockQuoteConfig(),
                blockQuoteIndent
        ));

        blockQuoteIndent -= 1;

        newLine();
    }

    @Override
    public void visit(Code code) {

        Debug.i(code);

        final int length = builder.length();

        // NB, in order to provide a _padding_ feeling code is wrapped inside two unbreakable spaces
        // unfortunately we cannot use this for multiline code as we cannot control there a new line break will be inserted
        builder.append('\u00a0');
        builder.append(code.getLiteral());
        builder.append('\u00a0');

        setSpan(length, new CodeSpan(
                configuration.getCodeConfig(),
                false
        ));
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {

        Debug.i(fencedCodeBlock);

        newLine();

        final int length = builder.length();

        builder.append(fencedCodeBlock.getLiteral());
        setSpan(length, new CodeSpan(
                configuration.getCodeConfig(),
                true
        ));

        newLine();
    }

    @Override
    public void visit(Image image) {

        Debug.i(image);

        final int length = builder.length();

        visitChildren(image);

        if (length == builder.length()) {
            // nothing is added, and we need at least one symbol
            builder.append(' ');
        }


////            final int length = builder.length();
//        final TestDrawable drawable = new TestDrawable();
//        final DrawableSpan span = new DrawableSpan(drawable);
//        builder.append("  ");
//        builder.setSpan(span, length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void visit(BulletList bulletList) {
        Debug.i(bulletList);
        newLine();
        visitChildren(bulletList);
        newLine();
    }

    @Override
    public void visit(ListItem listItem) {

        Debug.i(listItem);

        final int length = builder.length();
        blockQuoteIndent += 1;
        listLevel += 1;
        visitChildren(listItem);
        // todo, can be a bullet list & ordered list (with leading numbers... looks like we need to `draw` numbers...
        setSpan(length, new BulletListItemSpan(
                configuration.getBulletListConfig(),
                blockQuoteIndent,
                listLevel - 1,
                length
        ));
//        builder.setSpan(new BulletListItemSpan(blockQuoteIndent, listLevel > 1, length), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        blockQuoteIndent -= 1;
        listLevel -= 1;

        newLine();
    }

    @Override
    public void visit(ThematicBreak thematicBreak) {

        Debug.i(thematicBreak);

        newLine();

        // todo, new lines...
        final int length = builder.length();
        builder.append(' '); // without space it won't render
        builder.setSpan(new ThematicBreakSpan(), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        newLine();
    }

    @Override
    public void visit(OrderedList orderedList) {

        Debug.i(orderedList);

        newLine();

//        Debug.i(orderedList, orderedList.getDelimiter(), orderedList.getStartNumber());
        // todo, ordering numbers
        super.visit(orderedList);

        newLine();
    }

    @Override
    public void visit(Heading heading) {

        Debug.i(heading);

        newLine();

        final int length = builder.length();
        visitChildren(heading);
        setSpan(length, new HeadingSpan(
                configuration.getHeadingConfig(),
                heading.getLevel(),
                builder.length())
        );

        newLine();
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        Debug.i(softLineBreak);
        newLine();
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        Debug.i(hardLineBreak);
        newLine();
    }

    @Override
    public void visit(CustomNode customNode) {

        Debug.i(customNode);

        if (customNode instanceof Strikethrough) {
            final int length = builder.length();
            visitChildren(customNode);
            setSpan(length, new StrikethroughSpan());
        } else {
            super.visit(customNode);
        }
    }

    @Override
    public void visit(Paragraph paragraph) {

        final boolean inTightList = isInTightList(paragraph);

        Debug.i(paragraph, inTightList);

        if (!inTightList) {
            newLine();
        }

        visitChildren(paragraph);

        if (!inTightList) {
            newLine();
        }
    }

    @Override
    public void visit(HtmlBlock htmlBlock) {
        // http://spec.commonmark.org/0.18/#html-blocks
        Debug.i(htmlBlock, htmlBlock.getLiteral());
        super.visit(htmlBlock);
    }

    private void setSpan(int start, @NonNull Object span) {
        builder.setSpan(span, start, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void newLine() {
        if (builder.length() > 0
                && '\n' != builder.charAt(builder.length() - 1)) {
            builder.append('\n');
        }
    }

    private boolean isInTightList(Paragraph paragraph) {
        final Node parent = paragraph.getParent();
        if (parent != null) {
            final Node gramps = parent.getParent();
            if (gramps != null && gramps instanceof ListBlock) {
                ListBlock list = (ListBlock) gramps;
                return list.isTight();
            }
        }
        return false;
    }
}
