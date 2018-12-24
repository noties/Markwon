package ru.noties.markwon.sample.extension.recycler;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.node.AbstractVisitor;
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

import ru.noties.debug.Debug;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.recycler.MarkwonAdapter;
import ru.noties.markwon.sample.extension.R;

// do not use in real applications, this is just a showcase
public class TableNodeEntry implements MarkwonAdapter.Entry<TableNodeEntry.TableNodeHolder, TableBlock> {

    @NonNull
    @Override
    public TableNodeHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new TableNodeHolder(inflater.inflate(R.layout.adapter_table_block, parent, false));
    }

    @Override
    public void bindHolder(@NonNull Markwon markwon, @NonNull TableNodeHolder holder, @NonNull TableBlock node) {

        if (true) {
            Debug.e("###############");
            Debug.e("NODE: %s", node);
            node.accept(new PrintVisitor());
            Debug.e("NODE: %s", node);
            Debug.e("###############");
        }

        final TableLayout layout = holder.layout;
        layout.removeAllViews();

        // each child represents a row (head or regular)
        // first direct child is TableHead or TableBody
        Node child = node.getFirstChild().getFirstChild();
        Node temp;

        while (child != null) {
            Log.e("BIND-ROWS", String.valueOf(child));
            temp = child.getNext();
            addRow(markwon, layout, child);
            child = temp;
        }

        Log.e("BIND", String.valueOf(layout.getChildCount()));
        if (true) {
            final ViewGroup group = (ViewGroup) layout.getChildAt(0);
            Log.e("BIND-GROUP", String.valueOf(group.getChildCount()));
            for (int i = 0; i < group.getChildCount(); i++) {
                Log.e("BIND-CHILD-" + i, String.valueOf(group.getChildAt(i)) + ", " + ((TextView) group.getChildAt(i)).getText());
            }
        }

        layout.requestLayout();
    }

    private void addRow(@NonNull Markwon markwon, @NonNull TableLayout layout, @NonNull Node node) {

        final TableRow tableRow = new TableRow(layout.getContext());
//        final TableRow.LayoutParams params = new TableRow.LayoutParams(100, 100);
        tableRow.setBackgroundColor(0x80ff0000);

        TextView textView;
        RenderNode renderNode;
        Node temp;

        // each child in a row represents a cell
        Node child = node.getFirstChild();
        while (child != null) {
            Log.e("BIND-CELL", String.valueOf(child));
            textView = new TextView(layout.getContext());
            textView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            renderNode = new RenderNode();
            temp = child.getNext();
            copy(child, renderNode);
            tableRow.addView(textView);
            markwon.setParsedMarkdown(textView, markwon.render(renderNode));
            child = temp;
        }

        layout.addView(tableRow);
    }

    private static void copy(@NonNull Node from, @NonNull Node to) {
        Node child = from.getFirstChild();
        Node temp;
        while (child != null) {
            Log.e("BIND-COPY", String.valueOf(child));
            temp = child.getNext();
            to.appendChild(child);
            child = temp;
        }
    }

    @Override
    public long id(@NonNull TableBlock node) {
        return node.hashCode();
    }

    @Override
    public void clear() {

    }

    static class TableNodeHolder extends MarkwonAdapter.Holder {

        final TableLayout layout;

        TableNodeHolder(@NonNull View itemView) {
            super(itemView);

            this.layout = requireView(R.id.table_layout);
        }
    }

    private static class RenderNode extends CustomBlock {

    }

    private static class PrintVisitor extends AbstractVisitor {

        private final RenderNode renderNode = new RenderNode();

        @Override
        public void visit(BlockQuote blockQuote) {
            Debug.i("blockQuote: %s", blockQuote);
            super.visit(blockQuote);
        }

        @Override
        public void visit(BulletList bulletList) {
            Debug.i("bulletList: %s", bulletList);
            super.visit(bulletList);
        }

        @Override
        public void visit(Code code) {
            Debug.i("code: %s", code);
            super.visit(code);
        }

        @Override
        public void visit(Document document) {
            Debug.i("document: %s", document);
            super.visit(document);
        }

        @Override
        public void visit(Emphasis emphasis) {
            Debug.i("emphasis: %s", emphasis);
            super.visit(emphasis);
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            Debug.i("fencedCodeBlock: %s", fencedCodeBlock);
            super.visit(fencedCodeBlock);
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            Debug.i("hardLineBreak: %s", hardLineBreak);
            super.visit(hardLineBreak);
        }

        @Override
        public void visit(Heading heading) {
            Debug.i("heading: %s", heading);
            super.visit(heading);
        }

        @Override
        public void visit(ThematicBreak thematicBreak) {
            Debug.i("thematicBreak: %s", thematicBreak);
            super.visit(thematicBreak);
        }

        @Override
        public void visit(HtmlInline htmlInline) {
            Debug.i("htmlInline: %s", htmlInline);
            super.visit(htmlInline);
        }

        @Override
        public void visit(HtmlBlock htmlBlock) {
            Debug.i("htmlBlock: %s", htmlBlock);
            super.visit(htmlBlock);
        }

        @Override
        public void visit(Image image) {
            Debug.i("image: %s", image);
            super.visit(image);
        }

        @Override
        public void visit(IndentedCodeBlock indentedCodeBlock) {
            Debug.i("indentedCodeBlock: %s", indentedCodeBlock);
            super.visit(indentedCodeBlock);
        }

        @Override
        public void visit(Link link) {
            Debug.i("link: %s", link);
            super.visit(link);
        }

        @Override
        public void visit(ListItem listItem) {
            Debug.i("listItem: %s", listItem);
            super.visit(listItem);
        }

        @Override
        public void visit(OrderedList orderedList) {
            Debug.i("orderedList: %s", orderedList);
            super.visit(orderedList);
        }

        @Override
        public void visit(Paragraph paragraph) {
            Debug.i("paragraph: %s", paragraph);
            super.visit(paragraph);
        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            Debug.i("softLineBreak: %s", softLineBreak);
            super.visit(softLineBreak);
        }

        @Override
        public void visit(StrongEmphasis strongEmphasis) {
            Debug.i("strongEmphasis: %s", strongEmphasis);
            super.visit(strongEmphasis);
        }

        @Override
        public void visit(Text text) {
            Debug.i("text: %s", text);
            super.visit(text);
        }

        @Override
        public void visit(CustomBlock customBlock) {
            Debug.i("customBlock: %s", customBlock);
            super.visit(customBlock);
        }

        @Override
        public void visit(CustomNode customNode) {
            Debug.i("customNode: %s", customNode);
            super.visit(customNode);
        }
    }
}
