package ru.noties.markwon.core;

import android.support.annotation.NonNull;
import android.widget.TextView;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.core.visitor.BlockQuoteNodeVisitor;
import ru.noties.markwon.core.visitor.CodeBlockNodeVisitor;
import ru.noties.markwon.core.visitor.CodeNodeVisitor;
import ru.noties.markwon.core.visitor.EmphasisNodeVisitor;
import ru.noties.markwon.core.visitor.HardLineBreakNodeVisitor;
import ru.noties.markwon.core.visitor.HeadingNodeVisitor;
import ru.noties.markwon.core.visitor.LinkNodeVisitor;
import ru.noties.markwon.core.visitor.ListBlockNodeVisitor;
import ru.noties.markwon.core.visitor.ListItemNodeVisitor;
import ru.noties.markwon.core.visitor.ParagraphNodeVisitor;
import ru.noties.markwon.core.visitor.SoftLineBreakNodeVisitor;
import ru.noties.markwon.core.visitor.StrongEmphasisNodeVisitor;
import ru.noties.markwon.core.visitor.TextNodeVisitor;
import ru.noties.markwon.core.visitor.ThematicBreakNodeVisitor;
import ru.noties.markwon.spans.OrderedListItemSpan;

public class CorePlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static CorePlugin create() {
        return create(false);
    }

    @NonNull
    public static CorePlugin create(boolean softBreakAddsNewLine) {
        return new CorePlugin(softBreakAddsNewLine);
    }

    private final boolean softBreakAddsNewLine;

    protected CorePlugin(boolean softBreakAddsNewLine) {
        this.softBreakAddsNewLine = softBreakAddsNewLine;
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        text(builder);
        strongEmphasis(builder);
        emphasis(builder);
        blockQuote(builder);
        code(builder);
        fencedCodeBlock(builder);
        indentedCodeBlock(builder);
        bulletList(builder);
        orderedList(builder);
        listItem(builder);
        thematicBreak(builder);
        heading(builder);
        softLineBreak(builder);
        hardLineBreak(builder);
        paragraph(builder);
        link(builder);
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull CharSequence markdown) {
        OrderedListItemSpan.measure(textView, markdown);
        AsyncDrawableScheduler.unschedule(textView);
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        AsyncDrawableScheduler.schedule(textView);
    }

    protected void text(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Text.class, new TextNodeVisitor());
    }

    protected void strongEmphasis(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(StrongEmphasis.class, new StrongEmphasisNodeVisitor());
    }

    protected void emphasis(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Emphasis.class, new EmphasisNodeVisitor());
    }

    protected void blockQuote(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(BlockQuote.class, new BlockQuoteNodeVisitor());
    }

    protected void code(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Code.class, new CodeNodeVisitor());
    }

    protected void fencedCodeBlock(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(FencedCodeBlock.class, new CodeBlockNodeVisitor.Fenced());
    }

    protected void indentedCodeBlock(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(IndentedCodeBlock.class, new CodeBlockNodeVisitor.Indented());
    }

    protected void bulletList(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(BulletList.class, new ListBlockNodeVisitor());
    }

    protected void orderedList(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(OrderedList.class, new ListBlockNodeVisitor());
    }

    protected void listItem(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(ListItem.class, new ListItemNodeVisitor());
    }

    protected void thematicBreak(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(ThematicBreak.class, new ThematicBreakNodeVisitor());
    }

    protected void heading(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Heading.class, new HeadingNodeVisitor());
    }

    protected void softLineBreak(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(SoftLineBreak.class, new SoftLineBreakNodeVisitor(softBreakAddsNewLine));
    }

    protected void hardLineBreak(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(HardLineBreak.class, new HardLineBreakNodeVisitor());
    }

    protected void paragraph(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Paragraph.class, new ParagraphNodeVisitor());
    }

    protected void link(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Link.class, new LinkNodeVisitor());
    }
}
