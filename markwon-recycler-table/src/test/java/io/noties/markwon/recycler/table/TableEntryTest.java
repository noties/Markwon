package io.noties.markwon.recycler.table;

import android.content.res.Resources;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import io.noties.markwon.ext.tables.Table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TableEntryTest {

    @Test
    public void gravity() {
        // test textGravity is calculated correctly

        final List<Pair<Table.Alignment, Integer>> noVerticalAlign = Arrays.asList(
                new Pair<Table.Alignment, Integer>(Table.Alignment.LEFT, Gravity.LEFT),
                new Pair<Table.Alignment, Integer>(Table.Alignment.CENTER, Gravity.CENTER_HORIZONTAL),
                new Pair<Table.Alignment, Integer>(Table.Alignment.RIGHT, Gravity.RIGHT)
        );

        final List<Pair<Table.Alignment, Integer>> withVerticalAlign = Arrays.asList(
                new Pair<Table.Alignment, Integer>(Table.Alignment.LEFT, Gravity.LEFT | Gravity.CENTER_VERTICAL),
                new Pair<Table.Alignment, Integer>(Table.Alignment.CENTER, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL),
                new Pair<Table.Alignment, Integer>(Table.Alignment.RIGHT, Gravity.RIGHT | Gravity.CENTER_VERTICAL)
        );

        for (Pair<Table.Alignment, Integer> pair : noVerticalAlign) {
            assertEquals(pair.first.name(), pair.second.intValue(), TableEntry.textGravity(pair.first, false));
        }

        for (Pair<Table.Alignment, Integer> pair : withVerticalAlign) {
            assertEquals(pair.first.name(), pair.second.intValue(), TableEntry.textGravity(pair.first, true));
        }
    }

    @Test
    public void holder_no_table_layout_id() {
        // validate that holder correctly obtains TableLayout instance casting root view

        // root is not TableLayout
        try {
            new TableEntry.Holder(false, 0, mock(View.class));
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("Root view is not TableLayout"));
        }

        // root is TableLayout
        try {
            final TableLayout tableLayout = mock(TableLayout.class);
            final TableEntry.Holder h = new TableEntry.Holder(false, 0, tableLayout);
            assertEquals(tableLayout, h.tableLayout);
        } catch (IllegalStateException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void holder_with_table_layout_id() {

        // not found
        try {

            final View view = mock(View.class);
            // resources are used to obtain id name for proper error message
            when(view.getResources()).thenReturn(mock(Resources.class));
            new TableEntry.Holder(false, 1, view);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("No view with id"));
        }

        // found
        try {
            final TableLayout tableLayout = mock(TableLayout.class);
            final View view = mock(View.class);
            when(view.findViewById(3)).thenReturn(tableLayout);
            final TableEntry.Holder holder = new TableEntry.Holder(false, 3, view);
            assertEquals(tableLayout, holder.tableLayout);
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}