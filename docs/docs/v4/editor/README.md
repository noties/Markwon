# Editor <Badge text="4.2.0" />

<MavenBadge4 :artifact="'editor'" />

Markdown editing highlight for Android based on **Markwon**.

<style>
video {
    max-height: 82vh;
}
</style>

<video controls="true" loop="" :poster="$withBase('/assets/markwon-editor-preview.jpg')">
    <source :src="$withBase('/assets/markwon-editor.mp4')" type="video/mp4">
    You browser does not support mp4 playback, try downloading video file 
    <a :href="$withBase('/assets/markwon-editor.mp4')">directly</a>
</video>

## Getting started with editor

```java
// obtain Markwon instance
final Markwon markwon = Markwon.create(this);

// create editor
final MarkwonEditor editor = MarkwonEditor.create(markwon);

// set edit listener
editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
```

The code above _highlights_ in-place which is OK for relatively small markdown inputs.
If you wish to offload main thread and highlight in background use `withPreRender`
`MarkwonEditorTextWatcher`:

```java
editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
        editor,
        Executors.newCachedThreadPool(),
        editText));
```

`MarkwonEditorTextWatcher` automatically triggers markdown highlight when text in `EditText` changes.
But you still can invoke `MarkwonEditor` manually:

```java
editor.process(editText.getText());

// please note that MarkwonEditor operates on caller thread,
// if you wish to execute this operation in background - this method
// must be called from background thread
editor.preRender(editText.getText(), new MarkwonEditor.PreRenderResultListener() {
    @Override
    public void onPreRenderResult(@NonNull MarkwonEditor.PreRenderResult result) {
        // it's wise to check if rendered result is for the same input,
        // for example by matching raw input
        if (editText.getText().toString().equals(result.resultEditable().toString())) {
            
            // if you are in background thread do not forget
            // to execute dispatch in main thread
            result.dispatchTo(editText.getText());
        }
    }
});
```

:::warning Implementation Detail
It must be mentioned that highlight is implemented via text diff. Everything
that is present in raw markdown input but missing from rendered result is considered
to be _punctuation_.
:::

:::danger Tables and LaTeX
Tables and LaTeX nodes won't be rendered correctly. They will be treated as _punctuation_
as whole. This comes from their implementation - they are _mocked_ and do not present
in final result as text and thus cannot be _diffed_. 
:::

## Custom punctuation span

By default `MarkwonEditor` uses lighter text color of widget to customize punctuation.
If you wish to use a different span you can use `punctuationSpan` configuration step:

```java
final MarkwonEditor editor = MarkwonEditor.builder(Markwon.create(this))
        .punctuationSpan(CustomPunctuationSpan.class, CustomPunctuationSpan::new)
        .build();
```

```java
public class CustomPunctuationSpan extends ForegroundColorSpan {
    CustomPunctuationSpan() {
        super(0xFFFF0000); // RED
    }
}
```

## Additional handling

In order to additionally highlight portions of markdown input (for example make text wrapped with `**`
symbols **bold**) `EditHandler` can be used:

```java
final MarkwonEditor editor = MarkwonEditor.builder(Markwon.create(this))
        .useEditHandler(new AbstractEditHandler<StrongEmphasisSpan>() {
            @Override
            public void configurePersistedSpans(@NonNull PersistedSpans.Builder builder) {
                // Here we define which span is _persisted_ in EditText, it is not removed
                //  from EditText between text changes, but instead - reused (by changing
                //  position). Consider it as a cache for spans. We could use `StrongEmphasisSpan`
                //  here also, but I chose Bold to indicate that this span is not the same
                //  as in off-screen rendered markdown
                builder.persistSpan(Bold.class, Bold::new);
            }

            @Override
            public void handleMarkdownSpan(
                    @NonNull PersistedSpans persistedSpans,
                    @NonNull Editable editable,
                    @NonNull String input,
                    @NonNull StrongEmphasisSpan span,
                    int spanStart,
                    int spanTextLength) {
                // Unfortunately we cannot hardcode delimiters length here (aka spanTextLength + 4)
                //  because multiple inline markdown nodes can refer to the same text.
                //  For example, `**_~~hey~~_**` - we will receive `**_~~` in this method,
                //  and thus will have to manually find actual position in raw user input
                final MarkwonEditorUtils.Match match =
                        MarkwonEditorUtils.findDelimited(input, spanStart, "**", "__");
                if (match != null) {
                    editable.setSpan(
                            // we handle StrongEmphasisSpan and represent it with Bold in EditText
                            //  we still could use StrongEmphasisSpan, but it must be accessed
                            //  via persistedSpans
                            persistedSpans.get(Bold.class),
                            match.start(),
                            match.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
            }

            @NonNull
            @Override
            public Class<StrongEmphasisSpan> markdownSpanType() {
                return StrongEmphasisSpan.class;
            }
        })
        .build();
```
