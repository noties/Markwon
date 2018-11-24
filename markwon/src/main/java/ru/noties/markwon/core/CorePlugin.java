package ru.noties.markwon.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.spans.OrderedListItemSpan;

public class CorePlugin extends AbstractMarkwonPlugin {

    // todo: factory. Logically it must be here only, but in order to make spans
    // uniform in HTML (for example) we should expose it... Anyway, this factory _must_
    // include only _core_ spans

    // todo: softBreak adds new line should be here (or maybe removed even?)

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
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull SpannableBuilder builder) {
        OrderedListItemSpan.measure(textView, builder);
    }

    protected void text(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Text.class, new MarkwonVisitor.NodeVisitor<Text>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Text text) {
                visitor.builder().append(text.getLiteral());
            }
        });
    }

    protected void strongEmphasis(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(StrongEmphasis.class, new MarkwonVisitor.NodeVisitor<StrongEmphasis>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull StrongEmphasis strongEmphasis) {
                final int length = visitor.length();
                visitor.visitChildren(strongEmphasis);
                visitor.setSpans(length, visitor.configuration().factory().strongEmphasis());
            }
        });
    }

    protected void emphasis(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Emphasis.class, new MarkwonVisitor.NodeVisitor<Emphasis>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Emphasis emphasis) {
                final int length = visitor.length();
                visitor.visitChildren(emphasis);
                visitor.setSpans(length, visitor.configuration().factory().emphasis());
            }
        });
    }

    protected void blockQuote(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(BlockQuote.class, new MarkwonVisitor.NodeVisitor<BlockQuote>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull BlockQuote blockQuote) {

                visitor.ensureNewLine();

                if (visitor.blockQuoteIndent() > 0) {
                    visitor.forceNewLine();
                }

                final int length = visitor.length();
                visitor.incrementBlockQuoteIndent();
                visitor.visitChildren(blockQuote);
                visitor.setSpans(length, visitor.configuration().factory().blockQuote(visitor.theme()));
                visitor.decrementBlockQuoteIndent();

                if (visitor.hasNext(blockQuote)) {
                    visitor.ensureNewLine();
                    if (visitor.blockQuoteIndent() > 0) {
                        visitor.forceNewLine();
                    }
                }
            }
        });
    }

    protected void code(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Code.class, new MarkwonVisitor.NodeVisitor<Code>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Code code) {

                final int length = visitor.length();

                // NB, in order to provide a _padding_ feeling code is wrapped inside two unbreakable spaces
                // unfortunately we cannot use this for multiline code as we cannot control where a new line break will be inserted
                visitor.builder()
                        .append('\u00a0')
                        .append(code.getLiteral())
                        .append('\u00a0');

                visitor.setSpans(length, visitor.configuration().factory().code(visitor.theme(), false));
            }
        });
    }

    protected void fencedCodeBlock(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(FencedCodeBlock.class, new MarkwonVisitor.NodeVisitor<FencedCodeBlock>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull FencedCodeBlock fencedCodeBlock) {
                visitCodeBlock(visitor, fencedCodeBlock.getInfo(), fencedCodeBlock.getLiteral(), fencedCodeBlock);
            }
        });
    }

    protected void indentedCodeBlock(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(IndentedCodeBlock.class, new MarkwonVisitor.NodeVisitor<IndentedCodeBlock>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull IndentedCodeBlock indentedCodeBlock) {
                visitCodeBlock(visitor, null, indentedCodeBlock.getLiteral(), indentedCodeBlock);
            }
        });
    }

    protected void visitCodeBlock(
            @NonNull MarkwonVisitor visitor,
            @Nullable String info,
            @NonNull String code,
            @NonNull Node node) {

        visitor.ensureNewLine();

        final int length = visitor.length();

        visitor.builder()
                .append('\u00a0').append('\n')
                .append(visitor.configuration().syntaxHighlight().highlight(info, code));

        visitor.ensureNewLine();

        visitor.builder().append('\u00a0');

        visitor.setSpans(length, visitor.configuration().factory().code(visitor.theme(), true));

        if (visitor.hasNext(node)) {
            visitor.ensureNewLine();
            visitor.forceNewLine();
        }
    }

    protected void bulletList(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(BulletList.class, new MarkwonVisitor.NodeVisitor<BulletList>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull BulletList bulletList) {
                visitList(visitor, bulletList);
            }
        });
    }

    protected void orderedList(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(OrderedList.class, new MarkwonVisitor.NodeVisitor<OrderedList>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull OrderedList orderedList) {
                visitList(visitor, orderedList);
            }
        });
    }

    protected void visitList(@NonNull MarkwonVisitor visitor, @NonNull Node node) {

        visitor.ensureNewLine();

        visitor.visitChildren(node);

        if (visitor.hasNext(node)) {
            visitor.ensureNewLine();
            if (visitor.listLevel() == 0
                    && visitor.blockQuoteIndent() == 0) {
                visitor.forceNewLine();
            }
        }
    }

    protected void listItem(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(ListItem.class, new MarkwonVisitor.NodeVisitor<ListItem>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull ListItem listItem) {

                final int length = visitor.length();

                visitor.incrementBlockQuoteIndent();
                visitor.incrementListLevel();

                final Node parent = listItem.getParent();
                if (parent instanceof OrderedList) {

                    final int start = ((OrderedList) parent).getStartNumber();

                    visitor.visitChildren(listItem);
                    visitor.setSpans(length, visitor.configuration().factory().orderedListItem(visitor.theme(), start));


                    // after we have visited the children increment start number
                    final OrderedList orderedList = (OrderedList) parent;
                    orderedList.setStartNumber(orderedList.getStartNumber() + 1);

                } else {

                    visitor.visitChildren(listItem);
                    visitor.setSpans(length, visitor.configuration().factory().bulletListItem(visitor.theme(), visitor.listLevel() - 1));

                }

                visitor.decrementBlockQuoteIndent();
                visitor.decrementListLevel();

                if (visitor.hasNext(listItem)) {
                    visitor.ensureNewLine();
                }
            }
        });
    }

    protected void thematicBreak(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(ThematicBreak.class, new MarkwonVisitor.NodeVisitor<ThematicBreak>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull ThematicBreak thematicBreak) {

                visitor.ensureNewLine();

                final int length = visitor.length();

                // without space it won't render
                visitor.builder().append('\u00a0');

                visitor.setSpans(length, visitor.configuration().factory().thematicBreak(visitor.theme()));

                if (visitor.hasNext(thematicBreak)) {
                    visitor.ensureNewLine();
                    visitor.forceNewLine();
                }
            }
        });
    }
}
