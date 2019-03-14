# Recycler Table <Badge text="3.0.0" />

<MavenBadge :artifact="'recycler-table'" />

Artifact that provides [MarkwonAdapter.Entry](/docs/v3/recycler/) to render `TableBlock` inside 
Android-native `TableLayout` widget.

<img :src="$withBase('/assets/recycler-table-screenshot.png')" alt="screenshot" width="45%">
<br>
<small><em><sup>*</sup> It's possible to wrap `TableLayout` inside a `HorizontalScrollView` to include all table content</em></small>

---

Register instance of `TableEntry` with `MarkwonAdapter` to render TableBlocks:
```java
final MarkwonAdapter adapter = MarkwonAdapter.builder(R.layout.adapter_default_entry, R.id.text)
        .include(TableBlock.class, TableEntry.create(builder -> builder
                .tableLayout(R.layout.adapter_table_block, R.id.table_layout)
                .textLayoutIsRoot(R.layout.view_table_entry_cell)))
        .build();
```

`TableEntry` requires at least 2 arguments:
* `tableLayout` - layout with `TableLayout` inside
* `textLayout` - layout with `TextView` inside (represents independent table cell)

In case when required view is the root of layout specific builder methods can be used:
* `tableLayoutIsRoot(int)`
* `textLayoutIsRoot(int)`

If your layouts have different structure (for example wrap a `TableView` inside a `HorizontalScrollView`)
then you should use methods that accept ID of required view inside layout:
* `tableLayout(int, int)`
* `textLayout(int, int)`

---

To display `TableBlock` as a `TableLayout` specific `MarkwonPlugin` must be used: `TableEntryPlugin`.

:::warning
Do not use `TablePlugin` if you wish to display markdown tables via `TableEntry`. Use **TableEntryPlugin** instead
:::

`TableEntryPlugin` can reuse existing `TablePlugin` to make appearance of tables the same in both contexts:
when rendering _natively_ in a TextView and when rendering in RecyclerView with TableEntry.

* `TableEntryPlugin.create(Context)` - creates plugin with default `TableTheme`
* `TableEntryPlugin.create(TableTheme)` - creates plugin with provided `TableTheme`
* `TableEntryPlugin.create(TablePlugin.ThemeConfigure)` - creates plugin with theme configured by `ThemeConfigure`
* `TableEntryPlugin.create(TablePlugin)` - creates plugin with `TableTheme` used in provided `TablePlugin`

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(TableEntryPlugin.create(context))
        // other plugins
        .build();
```

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(TableEntryPlugin.create(builder -> builder
                .tableBorderWidth(0)
                .tableHeaderRowBackgroundColor(Color.RED)))
        // other plugins
        .build();
```

## Table with scrollable content

To stretch table columns to fit the width of screen or to make table scrollable when content exceeds screen width
this layout can be used:

```xml
<HorizontalScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:clipChildren="false"
    android:clipToPadding="false"
    android:paddingLeft="16dip"
    android:paddingTop="8dip"
    android:paddingRight="16dip"
    android:paddingBottom="8dip"
    android:scrollbarStyle="outsideInset">

    <TableLayout
        android:id="@+id/table_layout"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:stretchColumns="*" />

</HorizontalScrollView>
```