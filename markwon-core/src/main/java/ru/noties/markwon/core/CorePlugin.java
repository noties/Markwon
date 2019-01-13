package ru.noties.markwon.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
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
import org.commonmark.node.ListBlock;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonSpansFactory;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.core.factory.BlockQuoteSpanFactory;
import ru.noties.markwon.core.factory.CodeBlockSpanFactory;
import ru.noties.markwon.core.factory.CodeSpanFactory;
import ru.noties.markwon.core.factory.EmphasisSpanFactory;
import ru.noties.markwon.core.factory.HeadingSpanFactory;
import ru.noties.markwon.core.factory.LinkSpanFactory;
import ru.noties.markwon.core.factory.ListItemSpanFactory;
import ru.noties.markwon.core.factory.StrongEmphasisSpanFactory;
import ru.noties.markwon.core.factory.ThematicBreakSpanFactory;
import ru.noties.markwon.core.spans.OrderedListItemSpan;
import ru.noties.markwon.priority.Priority;

/**
 * @see CoreProps
 * @since 3.0.0
 */
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
        softLineBreak(builder, softBreakAddsNewLine);
        hardLineBreak(builder);
        paragraph(builder);
        link(builder);
    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {

        // reuse this one for both code-blocks (indent & fenced)
        final CodeBlockSpanFactory codeBlockSpanFactory = new CodeBlockSpanFactory();

        builder
                .setFactory(StrongEmphasis.class, new StrongEmphasisSpanFactory())
                .setFactory(Emphasis.class, new EmphasisSpanFactory())
                .setFactory(BlockQuote.class, new BlockQuoteSpanFactory())
                .setFactory(Code.class, new CodeSpanFactory())
                .setFactory(FencedCodeBlock.class, codeBlockSpanFactory)
                .setFactory(IndentedCodeBlock.class, codeBlockSpanFactory)
                .setFactory(ListItem.class, new ListItemSpanFactory())
                .setFactory(Heading.class, new HeadingSpanFactory())
                .setFactory(Link.class, new LinkSpanFactory())
                .setFactory(ThematicBreak.class, new ThematicBreakSpanFactory());
    }

    @NonNull
    @Override
    public Priority priority() {
        return Priority.none();
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
        OrderedListItemSpan.measure(textView, markdown);
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        // let's ensure that there is a movement method applied
        // we do it `afterSetText` so any user-defined movement method won't be
        // replaced (it should be done in `beforeSetText` or manually on a TextView)
        if (textView.getMovementMethod() == null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private static void text(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Text.class, new MarkwonVisitor.NodeVisitor<Text>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Text text) {
                visitor.builder().append(text.getLiteral());
            }
        });
    }

    private static void strongEmphasis(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(StrongEmphasis.class, new MarkwonVisitor.NodeVisitor<StrongEmphasis>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull StrongEmphasis strongEmphasis) {
                final int length = visitor.length();
                visitor.visitChildren(strongEmphasis);
                visitor.setSpansForNodeOptional(strongEmphasis, length);
            }
        });
    }

    private static void emphasis(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Emphasis.class, new MarkwonVisitor.NodeVisitor<Emphasis>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Emphasis emphasis) {
                final int length = visitor.length();
                visitor.visitChildren(emphasis);
                visitor.setSpansForNodeOptional(emphasis, length);
            }
        });
    }

    private static void blockQuote(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(BlockQuote.class, new MarkwonVisitor.NodeVisitor<BlockQuote>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull BlockQuote blockQuote) {

                visitor.ensureNewLine();

                final int length = visitor.length();

                visitor.visitChildren(blockQuote);
                visitor.setSpansForNodeOptional(blockQuote, length);

                if (visitor.hasNext(blockQuote)) {
                    visitor.ensureNewLine();
                    visitor.forceNewLine();
                }
            }
        });
    }

    private static void code(@NonNull MarkwonVisitor.Builder builder) {
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

                visitor.setSpansForNodeOptional(code, length);
            }
        });
    }

    private static void fencedCodeBlock(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(FencedCodeBlock.class, new MarkwonVisitor.NodeVisitor<FencedCodeBlock>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull FencedCodeBlock fencedCodeBlock) {
                visitCodeBlock(visitor, fencedCodeBlock.getInfo(), fencedCodeBlock.getLiteral(), fencedCodeBlock);
            }
        });
    }

    private static void indentedCodeBlock(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(IndentedCodeBlock.class, new MarkwonVisitor.NodeVisitor<IndentedCodeBlock>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull IndentedCodeBlock indentedCodeBlock) {
                visitCodeBlock(visitor, null, indentedCodeBlock.getLiteral(), indentedCodeBlock);
            }
        });
    }

    @VisibleForTesting
    static void visitCodeBlock(
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

        visitor.setSpansForNodeOptional(node, length);

        if (visitor.hasNext(node)) {
            visitor.ensureNewLine();
            visitor.forceNewLine();
        }
    }

    private static void bulletList(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(BulletList.class, new SimpleBlockNodeVisitor());
    }

    private static void orderedList(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(OrderedList.class, new SimpleBlockNodeVisitor());
    }

    private static void listItem(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(ListItem.class, new MarkwonVisitor.NodeVisitor<ListItem>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull ListItem listItem) {

                final int length = visitor.length();

                // it's important to visit children before applying render props (
                // we can have nested children, who are list items also, thus they will
                // override out props (if we set them before visiting children)
                visitor.visitChildren(listItem);

                final Node parent = listItem.getParent();
                if (parent instanceof OrderedList) {

                    final int start = ((OrderedList) parent).getStartNumber();

                    CoreProps.LIST_ITEM_TYPE.set(visitor.renderProps(), CoreProps.ListItemType.ORDERED);
                    CoreProps.ORDERED_LIST_ITEM_NUMBER.set(visitor.renderProps(), start);

                    // after we have visited the children increment start number
                    final OrderedList orderedList = (OrderedList) parent;
                    orderedList.setStartNumber(orderedList.getStartNumber() + 1);

                } else {
                    CoreProps.LIST_ITEM_TYPE.set(visitor.renderProps(), CoreProps.ListItemType.BULLET);
                    CoreProps.BULLET_LIST_ITEM_LEVEL.set(visitor.renderProps(), listLevel(listItem));
                }

                visitor.setSpansForNodeOptional(listItem, length);

                if (visitor.hasNext(listItem)) {
                    visitor.ensureNewLine();
                }
            }
        });
    }

    private static int listLevel(@NonNull Node node) {
        int level = 0;
        Node parent = node.getParent();
        while (parent != null) {
            if (parent instanceof ListItem) {
                level += 1;
            }
            parent = parent.getParent();
        }
        return level;
    }

    private static void thematicBreak(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(ThematicBreak.class, new MarkwonVisitor.NodeVisitor<ThematicBreak>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull ThematicBreak thematicBreak) {

                visitor.ensureNewLine();

                final int length = visitor.length();

                // without space it won't render
                visitor.builder().append('\u00a0');

                visitor.setSpansForNodeOptional(thematicBreak, length);

                if (visitor.hasNext(thematicBreak)) {
                    visitor.ensureNewLine();
                    visitor.forceNewLine();
                }
            }
        });
    }

    private static void heading(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Heading.class, new MarkwonVisitor.NodeVisitor<Heading>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Heading heading) {

                visitor.ensureNewLine();

                final int length = visitor.length();
                visitor.visitChildren(heading);

                CoreProps.HEADING_LEVEL.set(visitor.renderProps(), heading.getLevel());

                visitor.setSpansForNodeOptional(heading, length);

                if (visitor.hasNext(heading)) {
                    visitor.ensureNewLine();
                    visitor.forceNewLine();
                }
            }
        });
    }

    private static void softLineBreak(@NonNull MarkwonVisitor.Builder builder, final boolean softBreakAddsNewLine) {
        builder.on(SoftLineBreak.class, new MarkwonVisitor.NodeVisitor<SoftLineBreak>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull SoftLineBreak softLineBreak) {
                if (softBreakAddsNewLine) {
                    visitor.ensureNewLine();
                } else {
                    visitor.builder().append(' ');
                }
            }
        });
    }

    private static void hardLineBreak(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(HardLineBreak.class, new MarkwonVisitor.NodeVisitor<HardLineBreak>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull HardLineBreak hardLineBreak) {
                visitor.ensureNewLine();
            }
        });
    }

    private static void paragraph(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Paragraph.class, new MarkwonVisitor.NodeVisitor<Paragraph>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Paragraph paragraph) {

                final boolean inTightList = isInTightList(paragraph);

                if (!inTightList) {
                    visitor.ensureNewLine();
                }

                final int length = visitor.length();
                visitor.visitChildren(paragraph);

                CoreProps.PARAGRAPH_IS_IN_TIGHT_LIST.set(visitor.renderProps(), inTightList);

                // @since 1.1.1 apply paragraph span
                visitor.setSpansForNodeOptional(paragraph, length);

                if (!inTightList && visitor.hasNext(paragraph)) {
                    visitor.ensureNewLine();
                    visitor.forceNewLine();
                }
            }
        });
    }

    private static boolean isInTightList(@NonNull Paragraph paragraph) {
        final Node parent = paragraph.getParent();
        if (parent != null) {
            final Node gramps = parent.getParent();
            if (gramps instanceof ListBlock) {
                ListBlock list = (ListBlock) gramps;
                return list.isTight();
            }
        }
        return false;
    }

    private static void link(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(Link.class, new MarkwonVisitor.NodeVisitor<Link>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Link link) {

                final int length = visitor.length();
                visitor.visitChildren(link);

                final MarkwonConfiguration configuration = visitor.configuration();
                final String destination = configuration.urlProcessor().process(link.getDestination());

                CoreProps.LINK_DESTINATION.set(visitor.renderProps(), destination);

                visitor.setSpansForNodeOptional(link, length);
            }
        });
    }
}
