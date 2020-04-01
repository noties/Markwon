package io.noties.markwon.ext.tables;

import android.content.Context;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TableBody;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.gfm.tables.TableHead;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpannableBuilder;

/**
 * @since 3.0.0
 */
public class TablePlugin extends AbstractMarkwonPlugin {

    public interface ThemeConfigure {
        void configureTheme(@NonNull TableTheme.Builder builder);
    }

    /**
     * Factory method to create a {@link TablePlugin} with default {@link TableTheme} instance
     * (obtained via {@link TableTheme#create(Context)} method)
     *
     * @see #create(TableTheme)
     * @see #create(ThemeConfigure)
     */
    @NonNull
    public static TablePlugin create(@NonNull Context context) {
        return new TablePlugin(TableTheme.create(context));
    }

    @NonNull
    public static TablePlugin create(@NonNull TableTheme tableTheme) {
        return new TablePlugin(tableTheme);
    }

    @NonNull
    public static TablePlugin create(@NonNull ThemeConfigure themeConfigure) {
        final TableTheme.Builder builder = new TableTheme.Builder();
        themeConfigure.configureTheme(builder);
        return new TablePlugin(builder.build());
    }

    private final TableTheme theme;
    private final TableVisitor visitor;

    @SuppressWarnings("WeakerAccess")
    TablePlugin(@NonNull TableTheme tableTheme) {
        this.theme = tableTheme;
        this.visitor = new TableVisitor(tableTheme);
    }

    @NonNull
    public TableTheme theme() {
        return theme;
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.extensions(Collections.singleton(TablesExtension.create()));
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        visitor.configure(builder);
    }

    @Override
    public void beforeRender(@NonNull Node node) {
        // clear before rendering (as visitor has some internal mutable state)
        visitor.clear();
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
        TableRowsScheduler.unschedule(textView);
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        TableRowsScheduler.schedule(textView);
    }

    private static class TableVisitor {

        private final TableTheme tableTheme;

        private List<TableRowSpan.Cell> pendingTableRow;
        private boolean tableRowIsHeader;
        private int tableRows;

        TableVisitor(@NonNull TableTheme tableTheme) {
            this.tableTheme = tableTheme;
        }

        void clear() {
            pendingTableRow = null;
            tableRowIsHeader = false;
            tableRows = 0;
        }

        void configure(@NonNull MarkwonVisitor.Builder builder) {
            builder
                    // @since 4.1.1 we use TableBlock instead of TableBody to add new lines
                    .on(TableBlock.class, new MarkwonVisitor.NodeVisitor<TableBlock>() {
                        @Override
                        public void visit(@NonNull MarkwonVisitor visitor, @NonNull TableBlock tableBlock) {

                            visitor.blockStart(tableBlock);

                            final int length = visitor.length();

                            visitor.visitChildren(tableBlock);

                            // @since 4.3.1 apply table span for the full table
                            visitor.setSpans(length, new TableSpan());

                            visitor.blockEnd(tableBlock);
                        }
                    })
                    .on(TableBody.class, new MarkwonVisitor.NodeVisitor<TableBody>() {
                        @Override
                        public void visit(@NonNull MarkwonVisitor visitor, @NonNull TableBody tableBody) {
                            visitor.visitChildren(tableBody);
                            tableRows = 0;
                        }
                    })
                    .on(TableRow.class, new MarkwonVisitor.NodeVisitor<TableRow>() {
                        @Override
                        public void visit(@NonNull MarkwonVisitor visitor, @NonNull TableRow tableRow) {
                            visitRow(visitor, tableRow);
                        }
                    })
                    .on(TableHead.class, new MarkwonVisitor.NodeVisitor<TableHead>() {
                        @Override
                        public void visit(@NonNull MarkwonVisitor visitor, @NonNull TableHead tableHead) {
                            visitRow(visitor, tableHead);
                        }
                    })
                    .on(TableCell.class, new MarkwonVisitor.NodeVisitor<TableCell>() {
                        @Override
                        public void visit(@NonNull MarkwonVisitor visitor, @NonNull TableCell tableCell) {

                            final int length = visitor.length();

                            visitor.visitChildren(tableCell);

                            if (pendingTableRow == null) {
                                pendingTableRow = new ArrayList<>(2);
                            }

                            pendingTableRow.add(new TableRowSpan.Cell(
                                    tableCellAlignment(tableCell.getAlignment()),
                                    visitor.builder().removeFromEnd(length)
                            ));

                            tableRowIsHeader = tableCell.isHeader();
                        }
                    });
        }

        private void visitRow(@NonNull MarkwonVisitor visitor, @NonNull Node node) {

            final int length = visitor.length();

            visitor.visitChildren(node);

            if (pendingTableRow != null) {

                final SpannableBuilder builder = visitor.builder();

                // @since 2.0.0
                // we cannot rely on hasNext(TableHead) as it's not reliable
                // we must apply new line manually and then exclude it from tableRow span
                final boolean addNewLine;
                {
                    final int builderLength = builder.length();
                    addNewLine = builderLength > 0
                            && '\n' != builder.charAt(builderLength - 1);
                }

                if (addNewLine) {
                    visitor.forceNewLine();
                }

                // @since 1.0.4 Replace table char with non-breakable space
                // we need this because if table is at the end of the text, then it will be
                // trimmed from the final result
                builder.append('\u00a0');

                final Object span = new TableRowSpan(
                        tableTheme,
                        pendingTableRow,
                        tableRowIsHeader,
                        tableRows % 2 == 1);

                tableRows = tableRowIsHeader
                        ? 0
                        : tableRows + 1;

                visitor.setSpans(addNewLine ? length + 1 : length, span);

                pendingTableRow = null;
            }
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
    }
}
