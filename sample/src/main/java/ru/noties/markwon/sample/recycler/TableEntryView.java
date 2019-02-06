package ru.noties.markwon.sample.recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannedString;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.noties.markwon.ext.tables.Table;
import ru.noties.markwon.sample.R;

public class TableEntryView extends LinearLayout {

    // paint and rect to draw borders
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect rect = new Rect();

    private LayoutInflater inflater;

    private int rowEvenBackgroundColor;

    public TableEntryView(Context context) {
        super(context);
        init(context, null);
    }

    public TableEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        inflater = LayoutInflater.from(context);
        setOrientation(VERTICAL);

        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TableEntryView);
            try {

                rowEvenBackgroundColor = array.getColor(R.styleable.TableEntryView_tev_rowEvenBackgroundColor, 0);

                final int stroke = array.getDimensionPixelSize(R.styleable.TableEntryView_tev_borderWidth, 0);

                // half of requested
                final float strokeWidth = stroke > 0
                        ? stroke / 2.F
                        : context.getResources().getDisplayMetrics().density / 2.F;

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(strokeWidth);
                paint.setColor(array.getColor(R.styleable.TableEntryView_tev_borderColor, Color.BLACK));

                if (isInEditMode()) {
                    final String data = array.getString(R.styleable.TableEntryView_tev_debugData);
                    if (data != null) {

                        boolean first = true;

                        final List<Table.Row> rows = new ArrayList<>();
                        for (String row : data.split("\\|")) {
                            final List<Table.Column> columns = new ArrayList<>();
                            for (String column : row.split(",")) {
                                columns.add(new Table.Column(Table.Alignment.LEFT, new SpannedString(column)));
                            }
                            final boolean header = first;
                            first = false;
                            rows.add(new Table.Row(header, columns));
                        }
                        final Table table = new Table(rows);
                        setTable(table);
                    }
                }
            } finally {
                array.recycle();
            }
        }

        setWillNotDraw(false);
    }

    public void setTable(@NonNull Table table) {
        final List<Table.Row> rows = table.rows();
        for (int i = 0, size = rows.size(); i < size; i++) {
            addRow(i, rows.get(i));
        }
        requestLayout();
    }

    private void addRow(int index, @NonNull Table.Row row) {

        final ViewGroup group = ensureRow(index);

        final int backgroundColor = !row.header() && (index % 2) == 0
                ? rowEvenBackgroundColor
                : 0;

        group.setBackgroundColor(backgroundColor);

        final List<Table.Column> columns = row.columns();

        TextView textView;
        Table.Column column;

        for (int i = 0, size = columns.size(); i < size; i++) {
            textView = ensureCell(group, i);
            column = columns.get(i);
            textView.setGravity(textGravity(column.alignment()));
            textView.setText(column.content());
            textView.getPaint().setFakeBoldText(row.header());
        }

        group.requestLayout();
    }

    @NonNull
    private ViewGroup ensureRow(int index) {

        final int count = getChildCount();
        if (index >= count) {

            // count=0,index=1, diff=2
            // count=0,index=5, diff=6
            // count=1,index=2, diff=2
            int diff = index - count + 1;
            while (diff > 0) {
                addView(inflater.inflate(R.layout.view_table_entry_row, this, false));
                diff -= 1;
            }
        }

        return (ViewGroup) getChildAt(index);
    }

    @NonNull
    private TextView ensureCell(@NonNull ViewGroup group, int index) {

        final int count = group.getChildCount();
        if (index >= count) {
            int diff = index - count + 1;
            while (diff > 0) {
                group.addView(inflater.inflate(R.layout.view_table_entry_cell, group, false));
                diff -= 1;
            }
        }

        return (TextView) group.getChildAt(index);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int rows = getChildCount();
        if (rows == 0) {
            return;
        }

        // first draw the whole border
        rect.set(0, 0, getWidth(), getHeight());
        canvas.drawRect(rect, paint);

        ViewGroup group;
        View view;

        int top;

        for (int row = 0; row < rows; row++) {
            group = (ViewGroup) getChildAt(row);
            top = group.getTop();
            for (int col = 0, cols = group.getChildCount(); col < cols; col++) {
                view = group.getChildAt(col);
                rect.set(view.getLeft(), top + view.getTop(), view.getRight(), top + view.getBottom());
                canvas.drawRect(rect, paint);
            }
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

        return gravity;
    }
}
