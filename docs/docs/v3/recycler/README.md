# Recycler <Badge text="3.0.0" />

<LegacyWarning />

<MavenBadge :artifact="'recycler'" />

This artifact allows displaying markdown in a set of Android widgets
inside a RecyclerView. Can be useful when displaying lengthy markdown
content or **displaying certain markdown blocks inside specific widgets**.

```java
// create an adapter that will use a TextView for each block of markdown
// `createTextViewIsRoot` accepts a layout in which TextView is the root view
final MarkwonAdapter adapter = 
        MarkwonAdapter.createTextViewIsRoot(R.layout.adapter_default_entry);
```

```java
// `create` method accepts a layout with TextView and ID of a TextView
// which allows wrapping a TextView inside another widget or combine with other widgets
final MarkwonAdapter adapter = 
        MarkwonAdapter.create(R.layout.adapter_default_entry, R.id.text_view);

// initialize RecyclerView (LayoutManager, Decorations, etc)
final RecyclerView recyclerView = obtainRecyclerView();

// set adapter
recyclerView.setAdapter(adapter);

// obtain an instance of Markwon (register all required plugins)
final Markwon markwon = obtainMarkwon();

// set markdown to be displayed
adapter.setMarkdown(markwon, "# This is markdown!");

// NB, adapter does not handle updates on its own, please use
// whatever method appropriate for you.
adapter.notifyDataSetChanged();
```

Initialized adapter above will use a TextView for each markdown block.
In order to tell adapter to render certain blocks differently a `builder` can be used.
For example, let's render `FencedCodeBlock` inside a `HorizontalScrollView`:

```java
// we still need to have a _default_ entry
final MarkwonAdapter adapter =
        MarkwonAdapter.builderTextViewIsRoot(R.layout.adapter_default_entry)
                .include(FencedCodeBlock.class, new FencedCodeBlockEntry())
                .build();
```

where `FencedCodeBlockEntry` is:

```java
public class FencedCodeBlockEntry extends MarkwonAdapter.Entry<FencedCodeBlock, FencedCodeBlockEntry.Holder> {

    @NonNull
    @Override
    public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(inflater.inflate(R.layout.adapter_fenced_code_block, parent, false));
    }

    @Override
    public void bindHolder(@NonNull Markwon markwon, @NonNull Holder holder, @NonNull FencedCodeBlock node) {
        markwon.setParsedMarkdown(holder.textView, markwon.render(node));
    }

    public static class Holder extends MarkwonAdapter.Holder {

        final TextView textView;

        public Holder(@NonNull View itemView) {
            super(itemView);

            this.textView = requireView(R.id.text_view);
        }
    }
}
```

and its layout (`R.layout.adapter_fenced_code_block`):

```xml
<?xml version="1.0" encoding="utf-8"?>
<HorizontalScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:clipChildren="false"
    android:clipToPadding="false"
    android:fillViewport="true"
    android:paddingLeft="16dip"
    android:paddingRight="16dip"
    android:scrollbarStyle="outsideInset">

    <TextView
        android:id="@+id/text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#0f000000"
        android:fontFamily="monospace"
        android:lineSpacingExtra="2dip"
        android:paddingLeft="16dip"
        android:paddingTop="8dip"
        android:paddingRight="16dip"
        android:paddingBottom="8dip"
        android:textAppearance="?android:attr/textAppearanceMedium"
        android:textSize="14sp" />

</HorizontalScrollView>
```

As we apply styling to `FencedCodeBlock` _manually_, we no longer need
`Markwon` to apply styling spans for us, so `Markwon` initialization could be:

```java
final Markwon markwon = Markwon.builder(context)
        // your other plugins
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                builder.on(FencedCodeBlock.class, (visitor, fencedCodeBlock) -> {
                    // we actually won't be applying code spans here, as our custom view will
                    // draw background and apply mono typeface
                    //
                    // NB the `trim` operation on literal (as code will have a new line at the end)
                    final CharSequence code = visitor.configuration()
                            .syntaxHighlight()
                            .highlight(fencedCodeBlock.getInfo(), fencedCodeBlock.getLiteral().trim());
                    visitor.builder().append(code);
                });
            }
        })
        .build();
```

Previously we have created a `FencedCodeBlockEntry` but all it does is apply markdown to a TextView.
For such a case there is a `SimpleEntry` that could be used instead:

```java
final MarkwonAdapter adapter =
        MarkwonAdapter.builderTextViewIsRoot(R.layout.adapter_default_entry)
                .include(FencedCodeBlock.class, SimpleEntry.create(R.layout.adapter_fenced_code_block, R.id.text_view))
                .build();
```

:::tip
`SimpleEntry` also takes care of _caching_ parsed markdown. So each node will be
parsed only once and each subsequent adapter binding call will reuse previously cached markdown.
:::

:::tip Tables
There is a standalone artifact that adds support for displaying markdown tables
natively via `TableLayout`. Please refer to its [documentation](/docs/v3/recycler-table/)
:::