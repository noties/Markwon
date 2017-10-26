package ru.noties.markwon.renderer;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;

import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.ext.gfm.tables.TableBody;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomBlock;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.renderer.html.SpannableHtmlParser;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.AsyncDrawableSpan;
import ru.noties.markwon.spans.BlockQuoteSpan;
import ru.noties.markwon.spans.BulletListItemSpan;
import ru.noties.markwon.spans.CodeSpan;
import ru.noties.markwon.spans.EmphasisSpan;
import ru.noties.markwon.spans.HeadingSpan;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.OrderedListItemSpan;
import ru.noties.markwon.spans.StrongEmphasisSpan;
import ru.noties.markwon.spans.TableRowSpan;
import ru.noties.markwon.spans.TaskListSpan;
import ru.noties.markwon.spans.ThematicBreakSpan;
import ru.noties.markwon.tasklist.TaskListBlock;
import ru.noties.markwon.tasklist.TaskListItem;

@SuppressWarnings("WeakerAccess")
public class SpannableMarkdownVisitor extends AbstractVisitor {

    private final SpannableConfiguration mConfiguration;
    private final SpannableStringBuilder mBuilder;
    private final Deque<HtmlInlineItem> mHtmlInlineItems;

    private int mBlockQuoteIndent;
    private int mListLevel;

    private List<TableRowSpan.Cell> mPendingTableRow;
    private boolean mTableRowIsHeader;
    private int mTableRows;

    public SpannableMarkdownVisitor(
            @NonNull SpannableConfiguration configuration,
            @NonNull SpannableStringBuilder builder
    ) {
        mConfiguration = configuration;
        mBuilder = builder;
        mHtmlInlineItems = new ArrayDeque<>(2);
    }

    @Override
    public void visit(Text text) {
        mBuilder.append(text.getLiteral());
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        final int length = mBuilder.length();
        visitChildren(strongEmphasis);
        setSpan(length, new StrongEmphasisSpan());
    }

    @Override
    public void visit(Emphasis emphasis) {
        final int length = mBuilder.length();
        visitChildren(emphasis);
        setSpan(length, new EmphasisSpan());
    }

    @Override
    public void visit(BlockQuote blockQuote) {

        newLine();
        if (mBlockQuoteIndent != 0) {
            mBuilder.append('\n');
        }

        final int length = mBuilder.length();

        mBlockQuoteIndent += 1;

        visitChildren(blockQuote);

        setSpan(length, new BlockQuoteSpan(
                mConfiguration.theme(),
                mBlockQuoteIndent
        ));

        mBlockQuoteIndent -= 1;

        newLine();
        if (mBlockQuoteIndent == 0) {
            mBuilder.append('\n');
        }
    }

    @Override
    public void visit(Code code) {

        final int length = mBuilder.length();

        // NB, in order to provide a _padding_ feeling code is wrapped inside two unbreakable spaces
        // unfortunately we cannot use this for multiline code as we cannot control where a new line break will be inserted
        mBuilder.append('\u00a0');
        mBuilder.append(code.getLiteral());
        mBuilder.append('\u00a0');

        setSpan(length, new CodeSpan(
                mConfiguration.theme(),
                false
        ));
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {

        newLine();

        final int length = mBuilder.length();

        // empty lines on top & bottom
        mBuilder.append('\u00a0').append('\n');
        mBuilder.append(
                mConfiguration.syntaxHighlight()
                        .highlight(fencedCodeBlock.getInfo(), fencedCodeBlock.getLiteral())
        );
        mBuilder.append('\u00a0').append('\n');

        setSpan(length, new CodeSpan(
                mConfiguration.theme(),
                true
        ));

        newLine();
        mBuilder.append('\n');
    }

    @Override
    public void visit(BulletList bulletList) {
        visitList(bulletList);
    }

    @Override
    public void visit(OrderedList orderedList) {
        visitList(orderedList);
    }

    private void visitList(Node node) {
        newLine();
        visitChildren(node);
        newLine();
        if (mListLevel == 0 && mBlockQuoteIndent == 0) {
            mBuilder.append('\n');
        }
    }

    @Override
    public void visit(ListItem listItem) {

        final int length = mBuilder.length();

        mBlockQuoteIndent += 1;
        mListLevel += 1;

        final Node parent = listItem.getParent();
        if (parent instanceof OrderedList) {

            final int start = ((OrderedList) parent).getStartNumber();

            visitChildren(listItem);

            setSpan(length, new OrderedListItemSpan(
                    mConfiguration.theme(),
                    String.valueOf(start) + "." + '\u00a0',
                    mBlockQuoteIndent
            ));

            // after we have visited the children increment start number
            final OrderedList orderedList = (OrderedList) parent;
            orderedList.setStartNumber(orderedList.getStartNumber() + 1);

        } else {

            visitChildren(listItem);

            setSpan(length, new BulletListItemSpan(
                    mConfiguration.theme(),
                    mBlockQuoteIndent,
                    mListLevel - 1
            ));
        }

        mBlockQuoteIndent -= 1;
        mListLevel -= 1;

        newLine();
    }

    @Override
    public void visit(ThematicBreak thematicBreak) {

        newLine();

        final int length = mBuilder.length();
        mBuilder.append(' '); // without space it won't render
        setSpan(length, new ThematicBreakSpan(mConfiguration.theme()));

        newLine();
        mBuilder.append('\n');
    }

    @Override
    public void visit(Heading heading) {

        newLine();

        final int length = mBuilder.length();
        visitChildren(heading);
        setSpan(length, new HeadingSpan(
                mConfiguration.theme(),
                heading.getLevel(),
                mBuilder.length() - length)
        );

        newLine();

        // after heading we add another line anyway (no additional checks)
        mBuilder.append('\n');
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        // at first here was a new line, but here should be a space char
        mBuilder.append(' ');
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        newLine();
    }

    /**
     * @since 1.0.1
     */
    @Override
    public void visit(CustomBlock customBlock) {
        if (customBlock instanceof TaskListBlock) {
            mBlockQuoteIndent += 1;
            visitChildren(customBlock);
            mBlockQuoteIndent -= 1;
            newLine();
            mBuilder.append('\n');
        } else {
            super.visit(customBlock);
        }
    }

    @Override
    public void visit(CustomNode customNode) {

        if (customNode instanceof Strikethrough) {

            final int length = mBuilder.length();
            visitChildren(customNode);
            setSpan(length, new StrikethroughSpan());

        } else if (customNode instanceof TaskListItem) {

            // new in 1.0.1

            final TaskListItem listItem = (TaskListItem) customNode;

            final int length = mBuilder.length();

            mBlockQuoteIndent += listItem.indent();

            visitChildren(customNode);

            setSpan(length, new TaskListSpan(
                    mConfiguration.theme(),
                    mBlockQuoteIndent,
                    length,
                    listItem.done()
            ));

            newLine();

            mBlockQuoteIndent -= listItem.indent();

        } else if (!handleTableNodes(customNode)) {
            super.visit(customNode);
        }
    }

    private boolean handleTableNodes(CustomNode node) {

        final boolean handled;

        if (node instanceof TableBody) {
            visitChildren(node);
            mTableRows = 0;
            handled = true;
            newLine();
            mBuilder.append('\n');
        } else if (node instanceof TableRow) {

            final int length = mBuilder.length();
            visitChildren(node);

            if (mPendingTableRow != null) {
                mBuilder.append(' ');

                final TableRowSpan span = new TableRowSpan(
                        mConfiguration.theme(),
                        mPendingTableRow,
                        mTableRowIsHeader,
                        mTableRows % 2 == 1
                );

                mTableRows = mTableRowIsHeader
                        ? 0
                        : mTableRows + 1;

                setSpan(length, span);
                newLine();
                mPendingTableRow = null;
            }

            handled = true;
        } else if (node instanceof TableCell) {

            final TableCell cell = (TableCell) node;
            final int length = mBuilder.length();
            visitChildren(cell);
            if (mPendingTableRow == null) {
                mPendingTableRow = new ArrayList<>(2);
            }
            mPendingTableRow.add(new TableRowSpan.Cell(
                    tableCellAlignment(cell.getAlignment()),
                    mBuilder.subSequence(length, mBuilder.length())
            ));
            mBuilder.replace(length, mBuilder.length(), "");

            mTableRowIsHeader = cell.isHeader();

            handled = true;
        } else {
            handled = false;
        }
        return handled;
    }

    @Override
    public void visit(Paragraph paragraph) {

        final boolean inTightList = isInTightList(paragraph);

        if (!inTightList) {
            newLine();
        }

        visitChildren(paragraph);

        if (!inTightList) {
            newLine();

            if (mBlockQuoteIndent == 0) {
                mBuilder.append('\n');
            }
        }
    }

    @Override
    public void visit(Image image) {

        final int length = mBuilder.length();

        visitChildren(image);

        // we must check if anything _was_ added, as we need at least one char to render
        if (length == mBuilder.length()) {
            mBuilder.append('\uFFFC');
        }

        final Node parent = image.getParent();
        final boolean link = parent != null && parent instanceof Link;
        final String destination = mConfiguration.urlProcessor().process(image.getDestination());

        setSpan(
                length,
                new AsyncDrawableSpan(
                        mConfiguration.theme(),
                        new AsyncDrawable(
                                destination,
                                mConfiguration.asyncDrawableLoader()
                        ),
                        AsyncDrawableSpan.ALIGN_BOTTOM,
                        link
                )
        );

        setSpan(length, new ClickableSpan() {
            @Override
            public void onClick(View view) {
                mConfiguration.imageClickResolver().resolve(view, destination);
            }
        });
    }

    @Override
    public void visit(HtmlBlock htmlBlock) {
        // http://spec.commonmark.org/0.18/#html-blocks
        final Spanned spanned = mConfiguration.htmlParser().getSpanned(null, htmlBlock.getLiteral());
        if (!TextUtils.isEmpty(spanned)) {
            mBuilder.append(spanned);
        }
    }

    @Override
    public void visit(HtmlInline htmlInline) {

        final SpannableHtmlParser htmlParser = mConfiguration.htmlParser();
        final SpannableHtmlParser.Tag tag = htmlParser.parseTag(htmlInline.getLiteral());

        if (tag != null) {

            final boolean voidTag = tag.voidTag();
            if (!voidTag && tag.opening()) {
                // push in stack
                mHtmlInlineItems.push(new HtmlInlineItem(tag, mBuilder.length()));
                visitChildren(htmlInline);
            } else {

                if (!voidTag) {
                    if (mHtmlInlineItems.size() > 0) {
                        final HtmlInlineItem item = mHtmlInlineItems.pop();
                        final Object span = htmlParser.getSpanForTag(item.tag);
                        if (span != null) {
                            setSpan(item.start, span);
                        }
                    }
                } else {

                    final Spanned html = htmlParser.getSpanned(tag, htmlInline.getLiteral());
                    if (!TextUtils.isEmpty(html)) {
                        mBuilder.append(html);
                    }

                }
            }
        } else {
            // todo, should we append just literal?
//            mBuilder.append(htmlInline.getLiteral());
            visitChildren(htmlInline);
        }
    }

    @Override
    public void visit(Link link) {
        final int length = mBuilder.length();
        visitChildren(link);
        final String destination = mConfiguration.urlProcessor().process(link.getDestination());
        setSpan(length, new LinkSpan(mConfiguration.theme(), destination, mConfiguration.linkResolver()));
    }

    private void setSpan(int start, @NonNull Object span) {
        mBuilder.setSpan(span, start, mBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void newLine() {
        if (mBuilder.length() > 0
                && '\n' != mBuilder.charAt(mBuilder.length() - 1)) {
            mBuilder.append('\n');
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

    @TableRowSpan.Alignment
    private static int tableCellAlignment(TableCell.Alignment alignment) {
        final int out;
        if (alignment != null) {
            switch (alignment) {
                case CENTER:
                    out = TableRowSpan.ALIGN_CENTER;
                    break;
                case RIGHT:
                    out = TableRowSpan.ALIGN_RIGHT;
                    break;
                default:
                    out = TableRowSpan.ALIGN_LEFT;
                    break;
            }
        } else {
            out = TableRowSpan.ALIGN_LEFT;
        }
        return out;
    }

    private static class HtmlInlineItem {

        final SpannableHtmlParser.Tag tag;
        final int start;

        HtmlInlineItem(SpannableHtmlParser.Tag tag, int start) {
            this.tag = tag;
            this.start = start;
        }
    }
}
