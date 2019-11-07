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
If you wish to use a different span you can use `withPunctuationSpan` configuration step:

```java
final MarkwonEditor editor = MarkwonEditor.builder(Markwon.create(this))
        .withPunctuationSpan(CustomPunctuationSpan.class, CustomPunctuationSpan::new)
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
symbols **bold**) `EditSpanHandler` can be used:

```java
final MarkwonEditor editor = MarkwonEditor.builder(Markwon.create(this))
        // This is required for edit-span cache
        // We could use Markwon `StrongEmphasisSpan` here, but I use a different
        //  one to indicate that those are completely unrelated spans and must be
        //  treated differently.
        .includeEditSpan(Bold.class, Bold::new)
        .withEditSpanHandler(new MarkwonEditor.EditSpanHandler() {
            @Override
            public void handle(
                    @NonNull MarkwonEditor.EditSpanStore store,
                    @NonNull Editable editable,
                    @NonNull String input,
                    @NonNull Object span,
                    int spanStart,
                    int spanTextLength) {
                if (span instanceof StrongEmphasisSpan) {
                    editable.setSpan(
                            // `includeEditSpan(Bold.class, Bold::new)` ensured that we have
                            //      a span here to use (either reuse existing or create a new one)
                            store.get(Bold.class),
                            spanStart,
                            // we know that strong emphasis is delimited with 2 characters on both sides
                            spanStart + spanTextLength + 4,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
            }
        })
        .build();
```
