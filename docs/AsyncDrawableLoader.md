# AsyncDrawable.Loader

By default this library does not render any of the images. It's done to simplify rendering of text-based markdown. But if images must be supported, then the `AsyncDrawable.Loader` can be specified whilst building a `SpannableConfiguration` instance:

```java
final AsyncDrawable.Loader loader = new AsyncDrawable.Loader() {
    @Override
    public void load(@NonNull String destination, @NonNull final AsyncDrawable drawable) {
        // `download` method is here for demonstration purposes, it's not included in this interface
        download(destination, new Callback() {
            @Override
            public void onDownloaded(Drawable d) {
                // additionally we can call `drawable.isAttached()`
                // to ensure if AsyncDrawable is in layout
                drawable.setResult(d);
            }
        });
    }

    @Override
    public void cancel(@NonNull String destination) {
        // cancel download here
    }
};

// `this` here referrs to a Context instance
final SpannableConfiguration configuration = SpannableConfiguration.builder(this)
        .asyncDrawableLoader(loader)
        .build();
```

There is also standalone artifact that supports image loading *out-of-box* (including support for **SVG** & **GIF**), but provides little to none configuration and could be somewhat not optimal. Please refer to the [README][mil-readme] of the module.


### Contents

* [SpannableConfiguration]
* * [SpannableTheme]
* * [AsyncDrawableLoader]
* * [SyntaxHighlight]
* * [LinkResolver]
* * [UrlProcessor]
* * [HtmlParser]


[SpannableConfiguration]: ./SpannableConfiguration.md
[SpannableTheme]: ./SpannableTheme.md
[AsyncDrawableLoader]: ./AsyncDrawableLoader.md
[SyntaxHighlight]: ./SyntaxHighlight.md
[LinkResolver]: ./LinkResolver.md
[UrlProcessor]: ./UrlProcessor.md
[HtmlParser]: ./HtmlParser.md

[mil-readme]: ../library-image-loader/README.md
