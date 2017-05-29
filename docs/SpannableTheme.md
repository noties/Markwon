# SpannableTheme

`SpannableTheme` controls the appearance of rendered markdown. It has pretty reasonable defaults, which are established based on style of a TextView to which it is applied. It has some factory methods:
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