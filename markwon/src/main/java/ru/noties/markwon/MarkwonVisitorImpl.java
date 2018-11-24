package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomBlock;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.noties.markwon.spans.MarkwonTheme;

class MarkwonVisitorImpl implements MarkwonVisitor {

    private final Map<Class<? extends Node>, NodeVisitor<? extends Node>> nodes;

    private final MarkwonConfiguration configuration;
    private final MarkwonTheme theme;
    private final SpannableFactory factory;

    private final SpannableBuilder builder = new SpannableBuilder();

    private int blockQuoteIndent;
    private int listLevel;

    private MarkwonVisitorImpl(
            @NonNull MarkwonConfiguration configuration,
            @NonNull Map<Class<? extends Node>, NodeVisitor<? extends Node>> nodes) {
        this.configuration = configuration;
        this.theme = configuration.theme();
        this.factory = configuration.factory();
        this.nodes = nodes;
    }

    @Override
    public void visit(BlockQuote blockQuote) {
        visit((Node) blockQuote);
    }

    @Override
    public void visit(BulletList bulletList) {
        visit((Node) bulletList);
    }

    @Override
    public void visit(Code code) {
        visit((Node) code);
    }

    @Override
    public void visit(Document document) {
        visit((Node) document);
    }

    @Override
    public void visit(Emphasis emphasis) {
        visit((Node) emphasis);
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {
        visit((Node) fencedCodeBlock);
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        visit((Node) hardLineBreak);
    }

    @Override
    public void visit(Heading heading) {
        visit((Node) heading);
    }

    @Override
    public void visit(ThematicBreak thematicBreak) {
        visit((Node) thematicBreak);
    }

    @Override
    public void visit(HtmlInline htmlInline) {
        visit((Node) htmlInline);
    }

    @Override
    public void visit(HtmlBlock htmlBlock) {
        visit((Node) htmlBlock);
    }

    @Override
    public void visit(Image image) {
        visit((Node) image);
    }

    @Override
    public void visit(IndentedCodeBlock indentedCodeBlock) {
        visit((Node) indentedCodeBlock);
    }

    @Override
    public void visit(Link link) {
        visit((Node) link);
    }

    @Override
    public void visit(ListItem listItem) {
        visit((Node) listItem);
    }

    @Override
    public void visit(OrderedList orderedList) {
        visit((Node) orderedList);
    }

    @Override
    public void visit(Paragraph paragraph) {
        visit((Node) paragraph);
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        visit((Node) softLineBreak);
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        visit((Node) strongEmphasis);
    }

    @Override
    public void visit(Text text) {
        visit((Node) text);
    }

    @Override
    public void visit(CustomBlock customBlock) {
        visit((Node) customBlock);
    }

    @Override
    public void visit(CustomNode customNode) {
        visit((Node) customNode);
    }

    private void visit(@NonNull Node node) {
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
    public MarkwonTheme theme() {
        return theme;
    }

    @NonNull
    @Override
    public SpannableFactory factory() {
        return factory;
    }

    @NonNull
    @Override
    public SpannableBuilder builder() {
        return builder;
    }

    @Override
    public void visitChildren(@NonNull Node parent) {
        Node node = parent.getFirstChild();
        while (node != null) {
            // A subclass of this visitor might modify the node, resulting in getNext returning a different node or no
            // node after visiting it. So get the next node before visiting.
            Node next = node.getNext();
            node.accept(this);
            node = next;
        }
    }

    @Override
    public boolean hasNext(@NonNull Node node) {
        return node.getNext() != null;
    }

    @Override
    public void incrementBlockQuoteIndent() {
        blockQuoteIndent += 1;
    }

    @Override
    public void decrementBlockQuoteIndent() {
        blockQuoteIndent -= 1;
    }

    @Override
    public void blockQuoteIntent(int blockQuoteIndent) {
        this.blockQuoteIndent = blockQuoteIndent;
    }

    @Override
    public int blockQuoteIndent() {
        return blockQuoteIndent;
    }

    @Override
    public void incrementListLevel() {
        listLevel += 1;
    }

    @Override
    public void decrementListLevel() {
        listLevel -= 1;
    }

    @Override
    public int listLevel() {
        return listLevel;
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

    static class BuilderImpl implements Builder {

        private final Map<Class<? extends Node>, NodeVisitor<? extends Node>> nodes =
                new HashMap<>(3);

        @NonNull
        @Override
        public <N extends Node> Builder on(@NonNull Class<N> node, @NonNull NodeVisitor<N> nodeVisitor) {
            nodes.put(node, nodeVisitor);
            return this;
        }

        @NonNull
        @Override
        public MarkwonVisitor build(@NonNull MarkwonConfiguration configuration) {
            return new MarkwonVisitorImpl(
                    configuration,
                    Collections.unmodifiableMap(nodes));
        }
    }
}
