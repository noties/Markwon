package ru.noties.markwon.sample.extension.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import org.commonmark.ext.gfm.tables.TableBlock;

import java.util.HashMap;
import java.util.Map;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.ext.tables.Table;
import ru.noties.markwon.recycler.MarkwonAdapter;
import ru.noties.markwon.sample.extension.R;

// do not use in real applications, this is just a showcase
public class TableNodeEntry implements MarkwonAdapter.Entry<TableNodeEntry.TableNodeHolder, TableBlock> {

    private final Map<TableBlock, Table> cache = new HashMap<>(2);

    @NonNull
    @Override
    public TableNodeHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new TableNodeHolder(inflater.inflate(R.layout.adapter_table_block, parent, false));
    }

    @Override
    public void bindHolder(@NonNull Markwon markwon, @NonNull TableNodeHolder holder, @NonNull TableBlock node) {

        Table table = cache.get(node);
        if (table == null) {
            table = Table.parse(markwon, node);
            cache.put(node, table);
        }

        if (table != null) {
            // render table
            renderTable(markwon, holder, table);
        } // we need to do something with null table...
    }

    private void renderTable(
            @NonNull Markwon markwon,
            @NonNull TableNodeHolder holder,
            @NonNull Table table) {

    }

    @Override
    public long id(@NonNull TableBlock node) {
        return node.hashCode();
    }

    @Override
    public void clear() {
        cache.clear();
    }

    static class TableNodeHolder extends MarkwonAdapter.Holder {

        final TableLayout layout;

        TableNodeHolder(@NonNull View itemView) {
            super(itemView);

            this.layout = requireView(R.id.table_layout);
        }
    }
}
