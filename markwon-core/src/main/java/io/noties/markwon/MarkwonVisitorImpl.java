package io.noties.markwon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.BlockQuote;
import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.HardLineBreak;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.HtmlBlock;
import com.vladsch.flexmark.ast.HtmlCommentBlock;
import com.vladsch.flexmark.ast.HtmlEntity;
import com.vladsch.flexmark.ast.HtmlInline;
import com.vladsch.flexmark.ast.HtmlInlineComment;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.ImageRef;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.LinkRef;
import com.vladsch.flexmark.ast.ListItem;
import com.vladsch.flexmark.ast.MailLink;
import com.vladsch.flexmark.ast.OrderedList;
import com.vladsch.flexmark.ast.OrderedListItem;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.Reference;
import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ast.ThematicBreak;
import com.vladsch.flexmark.ast.util.BlockVisitorExt;
import com.vladsch.flexmark.ast.util.InlineVisitorExt;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 3.0.0
 */
class MarkwonVisitorImpl extends com.vladsch.flexmark.util.ast.NodeVisitor implements MarkwonVisitor {

    private final MarkwonConfiguration configuration;

    private final RenderProps renderProps;

    private final SpannableBuilder builder;

    private final Map<Class<? extends Node>, NodeVisitor<? extends Node>> nodes;

    // @since 4.3.0
    private final BlockHandler blockHandler;

    MarkwonVisitorImpl(
            @NonNull MarkwonConfiguration configuration,
            @NonNull RenderProps renderProps,
            @NonNull SpannableBuilder builder,
            @NonNull Map<Class<? extends Node>, NodeVisitor<? extends Node>> nodes,
            @NonNull BlockHandler blockHandler) {
        this.configuration = configuration;
        this.renderProps = renderProps;
        this.builder = builder;
        this.nodes = nodes;
        this.blockHandler = blockHandler;

        addHandlers(BlockVisitorExt.VISIT_HANDLERS(this));
        addHandlers(InlineVisitorExt.VISIT_HANDLERS(this));
    }

    @Override
    public void visit(BlockQuote blockQuote) {
        visitImpl(blockQuote);
    }

    @Override
    public void visit(BulletList bulletList) {
        visitImpl(bulletList);
    }

    @Override
    public void visit(AutoLink autoLink) {
        visitImpl(autoLink);
    }

    @Override
    public void visit(Code code) {
        visitImpl(code);
    }

    @Override
    public void visit(Document document) {
        visitImpl(document);
    }

    @Override
    public void visit(Emphasis emphasis) {
        visitImpl(emphasis);
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {
        visitImpl(fencedCodeBlock);
    }

    public void visit(HardLineBreak hardLineBreak) {
        visitImpl(hardLineBreak);
    }

    @Override
    public void visit(HtmlEntity node) {
        visitImpl(node);
    }

    @Override
    public void visit(Heading heading) {
        visitImpl(heading);
    }

    @Override
    public void visit(ThematicBreak thematicBreak) {
        visitImpl(thematicBreak);
    }

    @Override
    public void visit(HtmlInline htmlInline) {
        visitImpl(htmlInline);
    }

    @Override
    public void visit(HtmlInlineComment node) {
        visitImpl(node);
    }

    @Override
    public void visit(HtmlBlock htmlBlock) {
        visitImpl(htmlBlock);
    }

    @Override
    public void visit(HtmlCommentBlock node) {
        visitImpl(node);
    }

    /**
     * ![Alt text](/path/to/img.jpg "Optional title")
     */
    @Override
    public void visit(Image image) {
        visitImpl(image);
    }

    /**
     * This is a ![foo][bar] image.
     * [bar]: /url/of/bar.jpg "optional title attribute"
     */
    @Override
    public void visit(ImageRef imageRef) {
        visitImpl(imageRef);
    }

    @Override
    public void visit(IndentedCodeBlock indentedCodeBlock) {
        visitImpl(indentedCodeBlock);
    }


//    @Override
//    public void visit(ListItem listItem) {
//        visitImpl(listItem);
//    }

    @Override
    public void visit(BulletListItem listItem) {
        visitImpl(listItem);
    }

    @Override
    public void visit(OrderedListItem listItem) {
        visitImpl(listItem);
    }

    @Override
    public void visit(Link link) {
        visitImpl(link);
    }

    @Override
    public void visit(LinkRef node) {
        visitImpl(node);
    }

    @Override
    public void visit(MailLink node) {
        visitImpl(node);
    }

    @Override
    public void visit(OrderedList orderedList) {
        visitImpl(orderedList);
    }

    @Override
    public void visit(Paragraph paragraph) {
        visitImpl(paragraph);
    }

    @Override
    public void visit(Reference node) {
        // Reference 需要和 ImageRef,LinkRef配合使用，因为这些都是指向 Reference,再由Reference指向真正的地址
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        visitImpl(softLineBreak);
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        visitImpl(strongEmphasis);
    }

    @Override
    public void visit(Text text) {
        visitImpl(text);
    }

    private void visitImpl(@NonNull Node node) {
        //noinspection unchecked
        final NodeVisitor<Node> nodeVisitor = (NodeVisitor<Node>) nodes.get(node.getClass());
        if (nodeVisitor != null) {
            nodeVisitor.visit(this, node);
        } else {
            visitChildren(node);
        }
    }

    @NonNull
    @Override
    public MarkwonConfiguration configuration() {
        return configuration;
    }

    @NonNull
    @Override
    public RenderProps renderProps() {
        return renderProps;
    }

    @NonNull
    @Override
    public SpannableBuilder builder() {
        return builder;
    }


    @Override
    public boolean hasNext(@NonNull Node node) {
        return node.getNext() != null;
    }

    @Override
    public void ensureNewLine() {
        if (builder.length() > 0
                && '\n' != builder.lastChar()) {
            builder.append('\n');
        }
    }

    @Override
    public void forceNewLine() {
        builder.append('\n');
    }

    @Override
    public int length() {
        return builder.length();
    }

    @Override
    public void setSpans(int start, @Nullable Object spans) {
        SpannableBuilder.setSpans(builder, spans, start, builder.length());
    }

    @Override
    public void clear() {
        renderProps.clearAll();
        builder.clear();
    }

    @Override
    public <N extends Node> void setSpansForNode(@NonNull N node, int start) {
        setSpansForNode(node.getClass(), start);
    }

    @Override
    public <N extends Node> void setSpansForNode(@NonNull Class<N> node, int start) {
        setSpans(start, configuration.spansFactory().require(node).getSpans(configuration, renderProps));
    }

    @Override
    public <N extends Node> void setSpansForNodeOptional(@NonNull N node, int start) {
        setSpansForNodeOptional(node.getClass(), start);
    }

    @Override
    public <N extends Node> void setSpansForNodeOptional(@NonNull Class<N> node, int start) {
        final SpanFactory factory = configuration.spansFactory().get(node);
        if (factory != null) {
            setSpans(start, factory.getSpans(configuration, renderProps));
        }
    }

    @Override
    public void blockStart(@NonNull Node node) {
        blockHandler.blockStart(this, node);
    }

    @Override
    public void blockEnd(@NonNull Node node) {
        blockHandler.blockEnd(this, node);
    }

    static class BuilderImpl implements Builder {

        private final Map<Class<? extends Node>, NodeVisitor<? extends Node>> nodes = new HashMap<>();
        private BlockHandler blockHandler;

        @NonNull
        @Override
        public <N extends Node> Builder on(@NonNull Class<N> node, @Nullable NodeVisitor<? super N> nodeVisitor) {

            // @since 4.1.1 we might actually introduce a local flag to check if it's been built
            //  and throw an exception here if some modification is requested
            //  NB, as we might be built from different threads this flag must be synchronized

            // we should allow `null` to exclude node from being visited (for example to disable
            // some functionality)
            if (nodeVisitor == null) {
                nodes.remove(node);
            } else {
                nodes.put(node, nodeVisitor);
            }
            return this;
        }

        @NonNull
        @Override
        public Builder blockHandler(@NonNull BlockHandler blockHandler) {
            this.blockHandler = blockHandler;
            return this;
        }

        @NonNull
        @Override
        public MarkwonVisitor build(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps renderProps) {
            // @since 4.3.0
            BlockHandler blockHandler = this.blockHandler;
            if (blockHandler == null) {
                blockHandler = new BlockHandlerDef();
            }

            return new MarkwonVisitorImpl(
                    configuration,
                    renderProps,
                    new SpannableBuilder(),
                    Collections.unmodifiableMap(nodes),
                    blockHandler);
        }
    }
}
