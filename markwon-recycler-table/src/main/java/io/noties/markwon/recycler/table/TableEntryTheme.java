package io.noties.markwon.recycler.table;

import android.graphics.Paint;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Px;

import io.noties.markwon.ext.tables.TableTheme;
import io.noties.markwon.utils.ColorUtils;

/**
 * Mimics TableTheme to allow uniform table customization
 *
 * @see #create(TableTheme)
 * @see TableEntryPlugin
 * @since 3.0.0
 */
@SuppressWarnings("WeakerAccess")
public class TableEntryTheme extends TableTheme {

    @NonNull
    public static TableEntryTheme create(@NonNull TableTheme tableTheme) {
        return new TableEntryTheme(tableTheme.asBuilder());
    }

    protected TableEntryTheme(@NonNull Builder builder) {
        super(builder);
    }

    @Px
    @Override
    public int tableCellPadding() {
        return tableCellPadding;
    }

    @ColorInt
    public int tableBorderColor(@NonNull Paint paint) {
        return tableBorderColor == 0
                ? ColorUtils.applyAlpha(paint.getColor(), TABLE_BORDER_DEF_ALPHA)
                : tableBorderColor;
    }

    @Px
    @Override
    public int tableBorderWidth(@NonNull Paint paint) {
        return tableBorderWidth < 0
                ? (int) (paint.getStrokeWidth() + .5F)
                : tableBorderWidth;
    }

    @ColorInt
    public int tableOddRowBackgroundColor(@NonNull Paint paint) {
        return tableOddRowBackgroundColor == 0
                ? ColorUtils.applyAlpha(paint.getColor(), TABLE_ODD_ROW_DEF_ALPHA)
                : tableOddRowBackgroundColor;
    }

    @ColorInt
    public int tableEvenRowBackgroundColor() {
        return tableEvenRowBackgroundColor;
    }

    @ColorInt
    public int tableHeaderRowBackgroundColor() {
        return tableHeaderRowBackgroundColor;
    }
}
