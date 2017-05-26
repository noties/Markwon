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

Please note, that this library depends on [commonmark-java][commonmark-java] (and some extensions):
```groovy
compile 'com.atlassian.commonmark:commonmark:0.9.0'
compile 'com.atlassian.commonmark:commonmark-ext-gfm-strikethrough:0.9.0'
compile 'com.atlassian.commonmark:commonmark-ext-gfm-tables:0.9.0'
```

## Configuration
In order to render correctly markdown, this library need a `SpannableConfiguration` instance. It has 2 factory methods:
```java
// creates default instance
SpannableConfiguration.create(Context);

// returns configurable Builder
SpannableConfiguration.builder(Context);
```

`SpannableConfiguration.Builder` class has these configurable properties (which are described in more detail further):
```java
public Builder theme(SpannableTheme theme);
public Builder asyncDrawableLoader(AsyncDrawable.Loader asyncDrawableLoader);
public Builder syntaxHighlight(SyntaxHighlight syntaxHighlight);
public Builder linkResolver(LinkSpan.Resolver linkResolver);
public Builder urlProcessor(UrlProcessor urlProcessor);
public Builder htmlParser(SpannableHtmlParser htmlParser);

// and obviously:
public SpannableConfiguration build();
```

## Theme
`SpannableTheme` controlls the appearance of rendered markdown. It has pretty reasonable defaults, which are established based on style of a TextView to which it is applied. It has some factory methods:
```java
// creates ready-to-use SpannableThemeObject
SpannableTheme.create(Context);

// can be used to tweak default appearance
SpannableTheme.builderWithDefaults(Context);

// returns empty builder (no default values are set)
SpannableTheme.builder();

// returns a builder that is instantiated with all values from specified SpannableTheme
SpannableTheme.builder(SpannableTheme copyFrom);
```

`SpannableTheme.Builder` have these configurations:
#### Link
```java
public Builder linkColor(@ColorInt int linkColor);
```

#### Block
```java
// left margin for: lists & quotes (text is shifted)
public Builder blockMargin(@Dimension int blockMargin);
```

#### Quote
```java
// width of quote indication (the `|`)
public Builder blockQuoteWidth(@Dimension int blockQuoteWidth);

// color of `|` quote indication
public Builder blockQuoteColor(@ColorInt int blockQuoteColor);
```

#### Lists
```java
// color of list item bullets(●, ○, ■)/numbers
public Builder listItemColor(@ColorInt int listItemColor);

// stroke width for list bullet (2nd level - `○`)
public Builder bulletListItemStrokeWidth(@Dimension int bulletListItemStrokeWidth);

// width of list bullet (●, ○, ■)
public Builder bulletWidth(@Dimension int bulletWidth);
```

#### Code
```java
// text color for `code` blocks
public Builder codeTextColor(@ColorInt int codeTextColor);

// background color for `code` blocks
public Builder codeBackgroundColor(@ColorInt int codeBackgroundColor);

// left margin for multiline `code` blocks
public Builder codeMultilineMargin(@Dimension int codeMultilineMargin);

// typeface of `code` block
public Builder codeTypeface(@NonNull Typeface codeTypeface);

// text size for `code` block
public Builder codeTextSize(@Dimension int codeTextSize);
```

#### Headings
```java
// height of the `break` line under h1 & h2
public Builder headingBreakHeight(@Dimension int headingBreakHeight);

// color of the `break` line under h1 & h2
public Builder headingBreakColor(@ColorInt int headingBreakColor);
```

#### SuperScript & SupScript
```java
// ratio for <sup> & <sub> text size (calculated based on TextView text size)
public Builder scriptTextSizeRatio(@FloatRange(from = .0F, to = Float.MAX_VALUE) float scriptTextSizeRatio);
```

#### Thematic break
```java
// the `---` thematic break color
public Builder thematicBreakColor(@ColorInt int thematicBreakColor);

// the `---` thematic break height
public Builder thematicBreakHeight(@Dimension int thematicBreakHeight);
```

#### Tables
```java
// padding inside a table cell
public Builder tableCellPadding(@Dimension int tableCellPadding);

// color of table borders
public Builder tableBorderColor(@ColorInt int tableBorderColor);

// the `stroke` width of table border
public Builder tableBorderWidth(@Dimension int tableBorderWidth);

// the background of odd table rows
public Builder tableOddRowBackgroundColor(@ColorInt int tableOddRowBackgroundColor);
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

There is also standalone artifact that supports image loading *out-of-box* (including support for **SVG** & **GIF**), but provides little to none configuration and could be somewhat not optimal. Please refer to the [README][mil-README] of the module.


## Tables

Tables are supported but with some limitations. First of all: table will always take the full width of the TextView Canvas. Second: each column will have the same width (we do not calculate the weight of column) - so, a column width will be: `totalWidth / columnsNumber`.


## Syntax highlight
This library does not provide ready-to-be-used implementation of syntax highlight, but it can be added via `SyntaxHighlight` interface whilst building `SpannableConfiguration`:

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

## Link resolver
Link resolver is used to navigate to clicked link. By default `LinkResolverDef` is used and it just constructs an `Intent` and launches activity that can handle it, or silently fails if activity cannot be resolved. The main interface:
```java
public interface Resolver {
    void resolve(View view, @NonNull String link);
}
```

## HTML parser
As markdown supports HTML to be inlined, we need to introduce another entity that does (limited) parsing. Obtain an instance of `SpannableHtmlParser` via one of these factory methods:

```java
SpannableHtmlParser.create(SpannableTheme, AsyncDrawable.Loader)
SpannableHtmlParser.create(SpannableTheme, AsyncDrawable.Loader, UrlProcessor, LinkSpan.Resolver)
```

[commonmark-java]: https://github.com/atlassian/commonmark-java
[mil-README]: https://github.com/noties/Markwon/blob/master/library-image-loader/README.md
