package ru.noties.markwon.recycler.table;

import android.content.Context;
import androidx.annotation.NonNull;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;

import java.util.Collections;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.ext.tables.TablePlugin;
import ru.noties.markwon.ext.tables.TableTheme;

/**
 * This plugin must be used instead of {@link ru.noties.markwon.ext.tables.TablePlugin} when a markdown
 * table is intended to be used in a RecyclerView via {@link TableEntry}. This is required
 * because TablePlugin additionally processes markdown tables to be displayed in <em>limited</em>
 * context of a TextView. If TablePlugin will be used, {@link TableEntry} will display table,
 * but no content will be present
 *
 * @since 3.0.0
 */
public class TableEntryPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static TableEntryPlugin create(@NonNull Context context) {
        final TableTheme tableTheme = TableTheme.create(context);
        return create(tableTheme);
    }

    @NonNull
    public static TableEntryPlugin create(@NonNull TableTheme tableTheme) {
        return new TableEntryPlugin(TableEntryTheme.create(tableTheme));
    }

    @NonNull
    public static TableEntryPlugin create(@NonNull TablePlugin.ThemeConfigure themeConfigure) {
        final TableTheme.Builder builder = new TableTheme.Builder();
        themeConfigure.configureTheme(builder);
        return new TableEntryPlugin(new TableEntryTheme(builder));
    }

    @NonNull
    public static TableEntryPlugin create(@NonNull TablePlugin plugin) {
        return create(plugin.theme());
    }

    private final TableEntryTheme theme;

    @SuppressWarnings("WeakerAccess")
    TableEntryPlugin(@NonNull TableEntryTheme tableTheme) {
        this.theme = tableTheme;
    }

    @NonNull
    public TableEntryTheme theme() {
        return theme;
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.extensions(Collections.singleton(TablesExtension.create()));
    }
}
