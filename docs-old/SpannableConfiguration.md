# SpannableConfiguration

In order to render correctly markdown, this library needs a `SpannableConfiguration` instance. It has 2 factory methods:

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