package io.noties.markwon.ext.tables;

import android.content.Context;
import android.graphics.Paint;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Px;

import io.noties.markwon.utils.ColorUtils;
import io.noties.markwon.utils.Dip;

@SuppressWarnings("WeakerAccess")
public class TableTheme {

    @NonNull
    public static TableTheme create(@NonNull Context context) {
        return buildWithDefaults(context).build();
    }

    @NonNull
    public static Builder buildWithDefaults(@NonNull Context context) {
        final Dip dip = Dip.create(context);
        return emptyBuilder()
                .tableCellPadding(dip.toPx(4))
                .tableBorderWidth(dip.toPx(1));
    }

    @NonNull
    public static Builder emptyBuilder() {
        return new Builder();
    }


    protected static final int TABLE_BORDER_DEF_ALPHA = 75;

    protected static final int TABLE_ODD_ROW_DEF_ALPHA = 22;

    // by default 0
    protected final int tableCellPadding;

    // by default paint.color * TABLE_BORDER_DEF_ALPHA
    protected final int tableBorderColor;

    protected final int tableBorderWidth;

    // by default paint.color * TABLE_ODD_ROW_DEF_ALPHA
    protected final int tableOddRowBackgroundColor;

    // @since 1.1.1
    // by default no background
    protected final int tableEvenRowBackgroundColor;

    // @since 1.1.1
    // by default no background
    protected final int tableHeaderRowBackgroundColor;

    protected TableTheme(@NonNull Builder builder) {
        this.tableCellPadding = builder.tableCellPadding;
        this.tableBorderColor = builder.tableBorderColor;
        this.tableBorderWidth = builder.tableBorderWidth;
        this.tableOddRowBackgroundColor = builder.tableOddRowBackgroundColor;
        this.tableEvenRowBackgroundColor = builder.tableEvenRowBackgroundColor;
        this.tableHeaderRowBackgroundColor = builder.tableHeaderRowBackgroundColor;
    }

    /**
     * @since 3.0.0
     */
    @NonNull
    public Builder asBuilder() {
        return new Builder()
                .tableCellPadding(tableCellPadding)
                .tableBorderColor(tableBorderColor)
                .tableBorderWidth(tableBorderWidth)
                .tableOddRowBackgroundColor(tableOddRowBackgroundColor)
                .tableEvenRowBackgroundColor(tableEvenRowBackgroundColor)
                .tableHeaderRowBackgroundColor(tableHeaderRowBackgroundColor);
    }

    public int tableCellPadding() {
        return tableCellPadding;
    }

    public int tableBorderWidth(@NonNull Paint paint) {
        final int out;
        if (tableBorderWidth == -1) {
            out = (int) (paint.getStrokeWidth() + .5F);
        } else {
            out = tableBorderWidth;
        }
        return out;
    }

    public void applyTableBorderStyle(@NonNull Paint paint) {

        final int color;
        if (tableBorderColor == 0) {
            color = ColorUtils.applyAlpha(paint.getColor(), TABLE_BORDER_DEF_ALPHA);
        } else {
            color = tableBorderColor;
        }

        paint.setColor(color);
        // @since 4.3.1 before it was STROKE... change to FILL as we draw border differently
        paint.setStyle(Paint.Style.FILL);
    }

    public void applyTableOddRowStyle(@NonNull Paint paint) {
        final int color;
        if (tableOddRowBackgroundColor == 0) {
            color = ColorUtils.applyAlpha(paint.getColor(), TABLE_ODD_ROW_DEF_ALPHA);
        } else {
            color = tableOddRowBackgroundColor;
        }
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * @since 1.1.1
     */
    public void applyTableEvenRowStyle(@NonNull Paint paint) {
        // by default to background to even row
        paint.setColor(tableEvenRowBackgroundColor);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * @since 1.1.1
     */
    public void applyTableHeaderRowStyle(@NonNull Paint paint) {
        paint.setColor(tableHeaderRowBackgroundColor);
        paint.setStyle(Paint.Style.FILL);
    }

    public static class Builder {

        private int tableCellPadding;
        private int tableBorderColor;
        private int tableBorderWidth = -1;
        private int tableOddRowBackgroundColor;
        private int tableEvenRowBackgroundColor; // @since 1.1.1
        private int tableHeaderRowBackgroundColor; // @since 1.1.1

        @NonNull
        public Builder tableCellPadding(@Px int tableCellPadding) {
            this.tableCellPadding = tableCellPadding;
            return this;
        }

        @NonNull
        public Builder tableBorderColor(@ColorInt int tableBorderColor) {
            this.tableBorderColor = tableBorderColor;
            return this;
        }

        @NonNull
        public Builder tableBorderWidth(@Px int tableBorderWidth) {
            this.tableBorderWidth = tableBorderWidth;
            return this;
        }

        @NonNull
        public Builder tableOddRowBackgroundColor(@ColorInt int tableOddRowBackgroundColor) {
            this.tableOddRowBackgroundColor = tableOddRowBackgroundColor;
            return this;
        }

        @NonNull
        public Builder tableEvenRowBackgroundColor(@ColorInt int tableEvenRowBackgroundColor) {
            this.tableEvenRowBackgroundColor = tableEvenRowBackgroundColor;
            return this;
        }

        @NonNull
        public Builder tableHeaderRowBackgroundColor(@ColorInt int tableHeaderRowBackgroundColor) {
            this.tableHeaderRowBackgroundColor = tableHeaderRowBackgroundColor;
            return this;
        }

        @NonNull
        public TableTheme build() {
            return new TableTheme(this);
        }
    }
}
