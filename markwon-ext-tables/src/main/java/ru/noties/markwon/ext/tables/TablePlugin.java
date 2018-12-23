package ru.noties.markwon.ext.tables;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.widget.TextView;

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

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.SpannableBuilder;

public class TablePlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static TablePlugin create(@NonNull Context context) {
        return new TablePlugin(TableTheme.create(context));
    }

    @NonNull
    public static TablePlugin create(@NonNull TableTheme tableTheme) {
        return new TablePlugin(tableTheme);
    }

    private final TableTheme tableTheme;

    TablePlugin(@NonNull TableTheme tableTheme) {
        this.tableTheme = tableTheme;
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.extensions(Collections.singleton(TablesExtension.create()));
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        TableVisitor.configure(tableTheme, builder);
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

        static void configure(@NonNull TableTheme tableTheme, @NonNull MarkwonVisitor.Builder builder) {
            new TableVisitor(tableTheme, builder);
        }

        private final TableTheme tableTheme;

        private List<TableRowSpan.Cell> pendingTableRow;
        private boolean tableRowIsHeader;
        private int tableRows;

        private TableVisitor(@NonNull TableTheme tableTheme, @NonNull MarkwonVisitor.Builder builder) {
            this.tableTheme = tableTheme;
            builder
                    .on(TableBody.class, new MarkwonVisitor.NodeVisitor<TableBody>() {
                        @Override
                        public void visit(@NonNull MarkwonVisitor visitor, @NonNull TableBody tableBody) {

                            visitor.visitChildren(tableBody);
                            tableRows = 0;

                            if (visitor.hasNext(tableBody)) {
                                visitor.ensureNewLine();
                                visitor.forceNewLine();
                            }
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
