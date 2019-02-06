package ru.noties.markwon.sample.recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.commonmark.ext.gfm.tables.TableBlock;

import java.util.HashMap;
import java.util.Map;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.ext.tables.Table;
import ru.noties.markwon.recycler.MarkwonAdapter;
import ru.noties.markwon.sample.R;

public class TableEntry2 implements MarkwonAdapter.Entry<TableBlock, TableEntry2.TableHolder> {

    private final Map<TableBlock, Table> map = new HashMap<>(3);

    @NonNull
    @Override
    public TableHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new TableHolder(inflater.inflate(R.layout.adapter_table_block_2, parent, false));
    }

    @Override
    public void bindHolder(@NonNull Markwon markwon, @NonNull TableHolder holder, @NonNull TableBlock node) {

        Table table = map.get(node);
        if (table == null) {
            table = Table.parse(markwon, node);
            map.put(node, table);
        }

        // check if this exact TableBlock was already
        final TableLayout layout = holder.layout;
        if (table == null
                || table == layout.getTag(R.id.table_layout)) {
            return;
        }

        layout.setTag(R.id.table_layout, table);
        layout.removeAllViews();
        layout.setBackgroundResource(R.drawable.bg_table_cell);

        final Context context = layout.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);

        TableRow tableRow;
        TextView textView;

        for (Table.Row row : table.rows()) {
            tableRow = new TableRow(context);
            for (Table.Column column : row.columns()) {
                textView = (TextView) inflater.inflate(R.layout.view_table_entry_cell, tableRow, false);
                textView.setGravity(textGravity(column.alignment()));
                markwon.setParsedMarkdown(textView, column.content());
                textView.getPaint().setFakeBoldText(row.header());
                textView.setBackgroundResource(R.drawable.bg_table_cell);
                tableRow.addView(textView);
            }
            layout.addView(tableRow);
        }
    }

    @Override
    public long id(@NonNull TableBlock node) {
        return node.hashCode();
    }

    @Override
    public void clear() {
        map.clear();
    }

    static class TableHolder extends MarkwonAdapter.Holder {

        final TableLayout layout;

        TableHolder(@NonNull View itemView) {
            super(itemView);

            this.layout = requireView(R.id.table_layout);
        }
    }

    // we will use gravity instead of textAlignment because min sdk is 16 (textAlignment starts at 17)
    @SuppressLint("RtlHardcoded")
    private static int textGravity(@NonNull Table.Alignment alignment) {

        final int gravity;

        switch (alignment) {

            case LEFT:
                gravity = Gravity.LEFT;
                break;

            case CENTER:
                gravity = Gravity.CENTER_HORIZONTAL;
                break;

            case RIGHT:
                gravity = Gravity.RIGHT;
                break;

            default:
                throw new IllegalStateException("Unknown table alignment: " + alignment);
        }

        return gravity | Gravity.CENTER_VERTICAL;
    }
}
