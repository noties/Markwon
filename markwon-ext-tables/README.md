# Tables

[![ext-tables](https://img.shields.io/maven-central/v/ru.noties.markwon/ext-tables.svg?label=ext-tables)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties.markwon%22%20AND%20a%3A%22ext-tables%22)

This extension adds support for GFM tables.

```java
final Markwon markwon = Markwon.builder(context)
        // create default instance of TablePlugin
        .usePlugin(TablePlugin.create(context))
```

```java
final TableTheme tableTheme = TableTheme.builder()
        .tableBorderColor(Color.RED)
        .tableBorderWidth(0)
        .tableCellPadding(0)
        .tableHeaderRowBackgroundColor(Color.BLACK)
        .tableEvenRowBackgroundColor(Color.GREEN)
        .tableOddRowBackgroundColor(Color.YELLOW)
        .build();

final Markwon markwon = Markwon.builder(context)
        .usePlugin(TablePlugin.create(tableTheme))
```

Please note, that _by default_ tables have limitations. For example, there is no support
for images inside table cells. And table contents won't be copied to clipboard if a TextView
has such functionality. Table will always take full width of a TextView in which it is displayed.
All columns will always be the of the same width. So, _default_ implementation provides basic
functionality which can answer some needs. These all come from the limited nature of the TextView
to display such content.

In order to provide full-fledged experience, tables must be displayed in a special widget.
Since version `3.0.0` Markwon provides a special artifact `markwon-recycler` that allows
to render markdown in a set of widgets in a RecyclerView. It also gives ability to change
display widget form TextView to any other.

```java
final Table table = Table.parse(Markwon, TableBlock);
myTableWidget.setTable(table);
```

Unfortunately Markwon does not provide a widget that can be used for tables. But it does
provide API that can be used to achieve desired result.
