# Syntax highlight

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