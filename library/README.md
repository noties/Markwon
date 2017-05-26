# Markwon

[![maven|markwon](https://img.shields.io/maven-central/v/ru.noties/markwon.svg?label=maven%7Cmarkwon)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%markwon%22)


## Installation
```groovy
compile 'ru.noties:markwon:1.0.0'
```

## Intoduction

The aim for this library is to render markdown as first class citizen on Android - Spannables. It has reasonable defaults to display markdown, but also gives ability to customize almost every detail for your liking.

The most basic example would be:
```java
Markwon.setMarkdown(textView, "**Hello *there*!!**")
```

## Images

By default this library does not render any of the images. It's done to simplify rendering of text-based markdown. But if images must be supported, then the `AsyncDrawable.Loader` can be specified whilst building a `SpannableConfiguration` instance:

```java
final AsyncDrawable.Loader loader = new AsyncDrawable.Loader() {
    @Override
    public void load(@NonNull String destination, @NonNull final AsyncDrawable drawable) {
        // `download` method is here for demonstration purposes, it's not included in this interface
        download(destination, new Callback() {
            @Override
            public void onDownloaded(Drawable d) {
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

There is also standalone artifact that supports image loading *out-of-box* (including support for **SVG** & **GIF**), but provides little to none configuration and could be somewhat not optimal. Please refer to the [README][mil-README] of the module.


## Tables

Tables are supported but with some limitations. First of all: table will always take the full width of the TextView Canvas. Second: each column will have the same width (we do not calculate the weight of column) - so, a column width will be: `totalWidth / columnsNumber`.


## Syntax highlight
This library does not provide ready-to-be-used implementation of syntax highlight, but it can be easily added via `SyntaxHighlight` interface whilst building `SpannableConfiguration`:

```java
final SyntaxHighlight syntaxHighlight = new SyntaxHighlight() {
    @NonNull
    @Override
    public CharSequence highlight(@Nullable String info, @NonNull String code) {
        // create Spanned of highlight here
        return null; // must not return `null` here
    }
};

final SpannableConfiguration configuration = SpannableConfiguration.builder(this)
        .syntaxHighlight(syntaxHighlight)
        .build();
```

## Url processing
If you wish to process urls (links & images) that markdown contains, the `UrlProcessor` can be used:
```java
final UrlProcessor urlProcessor = new UrlProcessor() {
    @NonNull
    @Override
    public String process(@NonNull String destination) {
        // modify the `destination` or return as-is
        return null;
    }
};

final SpannableConfiguration configuration = SpannableConfiguration.builder(this)
        .urlProcessor(urlProcessor)
        .build();
```
The primary goal of additing this abstraction is to give ability to convert relative urls to absolute ones. If it fits your purpose, then `UrlProcessorRelativeToAbsolute` can be used:
```java
final UrlProcessor urlProcessor = new UrlProcessorRelativeToAbsolute("https://this-is-base.org");
```


[mil-README]: https://github.com/noties/Markwon/blob/master/library-image-loader/README.md
