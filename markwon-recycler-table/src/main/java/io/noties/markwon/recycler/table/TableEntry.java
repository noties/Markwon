package io.noties.markwon.recycler.table;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.annotation.VisibleForTesting;

import org.commonmark.ext.gfm.tables.TableBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.tables.Table;
import io.noties.markwon.recycler.MarkwonAdapter;
import io.noties.markwon.utils.NoCopySpannableFactory;

/**
 * @since 3.0.0
 */
public class TableEntry extends MarkwonAdapter.Entry<TableBlock, TableEntry.Holder> {

    public interface Builder {

        /**
         * @param tableLayoutResId layout with TableLayout
         * @param tableIdRes       id of the TableLayout inside specified layout
         * @see #tableLayoutIsRoot(int)
         */
        @NonNull
        Builder tableLayout(@LayoutRes int tableLayoutResId, @IdRes int tableIdRes);

        /**
         * @param tableLayoutResId layout with TableLayout as the root view
         * @see #tableLayout(int, int)
         */
        @NonNull
        Builder tableLayoutIsRoot(@LayoutRes int tableLayoutResId);

        /**
         * @param textLayoutResId layout with TextView
         * @param textIdRes       id of the TextView inside specified layout
         * @see #textLayoutIsRoot(int)
         */
        @NonNull
        Builder textLayout(@LayoutRes int textLayoutResId, @IdRes int textIdRes);

        /**
         * @param textLayoutResId layout with TextView as the root view
         * @see #textLayout(int, int)
         */
        @NonNull
        Builder textLayoutIsRoot(@LayoutRes int textLayoutResId);

        /**
         * @param cellTextCenterVertical if text inside a table cell should centered
         *                               vertically (by default `true`)
         */
        @NonNull
        Builder cellTextCenterVertical(boolean cellTextCenterVertical);

        /**
         * @param isRecyclable flag to set on RecyclerView.ViewHolder (by default `true`)
         */
        @NonNull
        Builder isRecyclable(boolean isRecyclable);

        @NonNull
        TableEntry build();
    }

    public interface BuilderConfigure {
        void configure(@NonNull Builder builder);
    }

    @NonNull
    public static Builder builder() {
        return new BuilderImpl();
    }

    @NonNull
    public static TableEntry create(@NonNull BuilderConfigure configure) {
        final Builder builder = builder();
        configure.configure(builder);
        return builder.build();
    }

    private final int tableLayoutResId;
    private final int tableIdRes;

    private final int textLayoutResId;
    private final int textIdRes;

    private final boolean isRecyclable;
    private final boolean cellTextCenterVertical; // by default true

    private LayoutInflater inflater;

    private final Map<TableBlock, Table> map = new HashMap<>(3);

    TableEntry(
            @LayoutRes int tableLayoutResId,
            @IdRes int tableIdRes,
            @LayoutRes int textLayoutResId,
            @IdRes int textIdRes,
            boolean isRecyclable,
            boolean cellTextCenterVertical) {
        this.tableLayoutResId = tableLayoutResId;
        this.tableIdRes = tableIdRes;
        this.textLayoutResId = textLayoutResId;
        this.textIdRes = textIdRes;
        this.isRecyclable = isRecyclable;
        this.cellTextCenterVertical = cellTextCenterVertical;
    }

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(
                isRecyclable,
                tableIdRes,
                inflater.inflate(tableLayoutResId, parent, false));
    }

    @Override
    public void bindHolder(@NonNull Markwon markwon, @NonNull Holder holder, @NonNull TableBlock node) {

        Table table = map.get(node);
        if (table == null) {
            table = Table.parse(markwon, node);
            map.put(node, table);
        }

        // check if this exact TableBlock was already applied
        // set tag of tableLayoutResId as it's 100% to be present (we still allow 0 as
        // tableIdRes if tableLayoutResId has TableLayout as root view)
        final TableLayout layout = holder.tableLayout;
        if (table == null
                || table == layout.getTag(tableLayoutResId)) {
            return;
        }

        // set this flag to indicate what table instance we current display
        layout.setTag(tableLayoutResId, table);

        final TableEntryPlugin plugin = markwon.getPlugin(TableEntryPlugin.class);
        if (plugin == null) {
            throw new IllegalStateException("No TableEntryPlugin is found. Make sure that it " +
                    "is _used_ whilst configuring Markwon instance");
        }

        // we must remove unwanted ones (rows and columns)

        final TableEntryTheme theme = plugin.theme();
        final int borderWidth;
        final int borderColor;
        final int cellPadding;
        {
            final TextView textView = ensureTextView(layout, 0, 0);
            borderWidth = theme.tableBorderWidth(textView.getPaint());
            borderColor = theme.tableBorderColor(textView.getPaint());
            cellPadding = theme.tableCellPadding();
        }

        ensureTableBorderBackground(layout, borderWidth, borderColor);

        //noinspection SuspiciousNameCombination
//        layout.setPadding(borderWidth, borderWidth, borderWidth, borderWidth);
//        layout.setClipToPadding(borderWidth == 0);

        final List<Table.Row> rows = table.rows();

        final int rowsSize = rows.size();

        // all rows should have equal number of columns
        final int columnsSize = rowsSize > 0
                ? rows.get(0).columns().size()
                : 0;

        Table.Row row;
        Table.Column column;

        TableRow tableRow;

        for (int y = 0; y < rowsSize; y++) {

            row = rows.get(y);
            tableRow = ensureRow(layout, y);

            for (int x = 0; x < columnsSize; x++) {

                column = row.columns().get(x);

                final TextView textView = ensureTextView(layout, y, x);
                textView.setGravity(textGravity(column.alignment(), cellTextCenterVertical));
                textView.getPaint().setFakeBoldText(row.header());

                // apply padding only if not specified in theme (otherwise just use the value from layout)
                if (cellPadding > 0) {
                    textView.setPadding(cellPadding, cellPadding, cellPadding, cellPadding);
                }

                ensureTableBorderBackground(textView, borderWidth, borderColor);
                markwon.setParsedMarkdown(textView, column.content());
            }

            // row appearance
            if (row.header()) {
                tableRow.setBackgroundColor(theme.tableHeaderRowBackgroundColor());
            } else {
                // as we currently have no support for tables without head
                // we shift even/odd calculation a bit (head should not be included in even/odd calculation)
                final boolean isEven = (y % 2) == 1;
                if (isEven) {
                    tableRow.setBackgroundColor(theme.tableEvenRowBackgroundColor());
                } else {
                    // just take first
                    final TextView textView = ensureTextView(layout, y, 0);
                    tableRow.setBackgroundColor(
                            theme.tableOddRowBackgroundColor(textView.getPaint()));
                }
            }
        }

        // clean up here of un-used rows and columns
        removeUnused(layout, rowsSize, columnsSize);
    }

    @NonNull
    private TableRow ensureRow(@NonNull TableLayout layout, int row) {

        final int count = layout.getChildCount();

        // fill the requested views until we have added the `row` one
        if (row >= count) {

            final Context context = layout.getContext();

            int diff = row - count + 1;
            while (diff > 0) {
                layout.addView(new TableRow(context));
                diff -= 1;
            }
        }

        // return requested child (here it always should be the last one)
        return (TableRow) layout.getChildAt(row);
    }

    @NonNull
    private TextView ensureTextView(@NonNull TableLayout layout, int row, int column) {

        final TableRow tableRow = ensureRow(layout, row);
        final int count = tableRow.getChildCount();

        if (column >= count) {

            final LayoutInflater inflater = ensureInflater(layout.getContext());

            boolean textViewChecked = false;

            View view;
            TextView textView;
            ViewGroup.LayoutParams layoutParams;

            int diff = column - count + 1;

            while (diff > 0) {

                view = inflater.inflate(textLayoutResId, tableRow, false);

                // we should have `match_parent` as height (important for borders and text-vertical-align)
                layoutParams = view.getLayoutParams();
                if (layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT) {
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                }

                // it will be enough to check only once
                if (!textViewChecked) {

                    if (textIdRes == 0) {
                        if (!(view instanceof TextView)) {
                            final String name = layout.getContext().getResources().getResourceName(textLayoutResId);
                            throw new IllegalStateException(String.format("textLayoutResId(R.layout.%s) " +
                                    "has other than TextView root view. Specify TextView ID explicitly", name));
                        }
                        textView = (TextView) view;
                    } else {
                        textView = view.findViewById(textIdRes);
                        if (textView == null) {
                            final Resources r = layout.getContext().getResources();
                            final String layoutName = r.getResourceName(textLayoutResId);
                            final String idName = r.getResourceName(textIdRes);
                            throw new NullPointerException(String.format("textLayoutResId(R.layout.%s) " +
                                    "has no TextView found by id(R.id.%s): %s", layoutName, idName, view));
                        }
                    }
                    // mark as checked
                    textViewChecked = true;
                } else {
                    if (textIdRes == 0) {
                        textView = (TextView) view;
                    } else {
                        textView = view.findViewById(textIdRes);
                    }
                }

                // we should set SpannableFactory during creation (to avoid another setText method)
                textView.setSpannableFactory(NoCopySpannableFactory.getInstance());
                tableRow.addView(textView);

                diff -= 1;
            }
        }

        // we can skip all the validation here as we have validated our views whilst inflating them
        final View last = tableRow.getChildAt(column);
        if (textIdRes == 0) {
            return (TextView) last;
        } else {
            return last.findViewById(textIdRes);
        }
    }

    private void ensureTableBorderBackground(@NonNull View view, @Px int borderWidth, @ColorInt int borderColor) {
        if (borderWidth == 0) {
            view.setBackground(null);
        } else {
            final Drawable drawable = view.getBackground();
            if (!(drawable instanceof TableBorderDrawable)) {
                final TableBorderDrawable borderDrawable = new TableBorderDrawable();
                borderDrawable.update(borderWidth, borderColor);
                view.setBackground(borderDrawable);
            } else {
                ((TableBorderDrawable) drawable).update(borderWidth, borderColor);
            }
        }
    }

    @NonNull
    private LayoutInflater ensureInflater(@NonNull Context context) {
        if (inflater == null) {
            inflater = LayoutInflater.from(context);
        }
        return inflater;
    }

    @SuppressWarnings("WeakerAccess")
    @VisibleForTesting
    static void removeUnused(@NonNull TableLayout layout, int usedRows, int usedColumns) {

        // clean up rows
        final int rowsCount = layout.getChildCount();
        if (rowsCount > usedRows) {
            layout.removeViews(usedRows, (rowsCount - usedRows));
        }

        // validate columns
        // here we can use usedRows as children count

        TableRow tableRow;
        int columnCount;

        for (int i = 0; i < usedRows; i++) {
            tableRow = (TableRow) layout.getChildAt(i);
            columnCount = tableRow.getChildCount();
            if (columnCount > usedColumns) {
                tableRow.removeViews(usedColumns, (columnCount - usedColumns));
            }
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    public static class Holder extends MarkwonAdapter.Holder {

        final TableLayout tableLayout;

        public Holder(boolean isRecyclable, @IdRes int tableLayoutIdRes, @NonNull View itemView) {
            super(itemView);

            // we must call this method only once (it's somehow _paired_ inside, so
            // any call in `onCreateViewHolder` or `onBindViewHolder` will log an error
            // `isRecyclable decremented below 0` which make little sense here)
            setIsRecyclable(isRecyclable);

            final TableLayout tableLayout;
            if (tableLayoutIdRes == 0) {
                // try to cast directly
                if (!(itemView instanceof TableLayout)) {
                    throw new IllegalStateException("Root view is not TableLayout. Please provide " +
                            "TableLayout ID explicitly");
                }
                tableLayout = (TableLayout) itemView;
            } else {
                tableLayout = requireView(tableLayoutIdRes);
            }
            this.tableLayout = tableLayout;
        }
    }

    // we will use gravity instead of textAlignment because min sdk is 16 (textAlignment starts at 17)
    @SuppressWarnings("WeakerAccess")
    @SuppressLint("RtlHardcoded")
    @VisibleForTesting
    static int textGravity(@NonNull Table.Alignment alignment, boolean cellTextCenterVertical) {

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

        if (cellTextCenterVertical) {
            return gravity | Gravity.CENTER_VERTICAL;
        }

        // do not center vertically
        return gravity;
    }

    static class BuilderImpl implements Builder {

        private int tableLayoutResId;
        private int tableIdRes;

        private int textLayoutResId;
        private int textIdRes;

        private boolean cellTextCenterVertical = true;

        private boolean isRecyclable = true;

        @NonNull
        @Override
        public Builder tableLayout(int tableLayoutResId, int tableIdRes) {
            this.tableLayoutResId = tableLayoutResId;
            this.tableIdRes = tableIdRes;
            return this;
        }

        @NonNull
        @Override
        public Builder tableLayoutIsRoot(int tableLayoutResId) {
            this.tableLayoutResId = tableLayoutResId;
            this.tableIdRes = 0;
            return this;
        }

        @NonNull
        @Override
        public Builder textLayout(int textLayoutResId, int textIdRes) {
            this.textLayoutResId = textLayoutResId;
            this.textIdRes = textIdRes;
            return this;
        }

        @NonNull
        @Override
        public Builder textLayoutIsRoot(int textLayoutResId) {
            this.textLayoutResId = textLayoutResId;
            this.textIdRes = 0;
            return this;
        }

        @NonNull
        @Override
        public Builder cellTextCenterVertical(boolean cellTextCenterVertical) {
            this.cellTextCenterVertical = cellTextCenterVertical;
            return this;
        }

        @NonNull
        @Override
        public Builder isRecyclable(boolean isRecyclable) {
            this.isRecyclable = isRecyclable;
            return this;
        }

        @NonNull
        @Override
        public TableEntry build() {

            if (tableLayoutResId == 0) {
                throw new IllegalStateException("`tableLayoutResId` argument is required");
            }

            if (textLayoutResId == 0) {
                throw new IllegalStateException("`textLayoutResId` argument is required");
            }

            return new TableEntry(
                    tableLayoutResId, tableIdRes,
                    textLayoutResId, textIdRes,
                    isRecyclable, cellTextCenterVertical
            );
        }
    }
}
