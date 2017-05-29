# HtmlParser

As markdown supports HTML to be inlined, we need to introduce another entity that does (limited) parsing. Obtain an instance of `SpannableHtmlParser` via one of these factory methods:

```java
SpannableHtmlParser.create(SpannableTheme, AsyncDrawable.Loader)
SpannableHtmlParser.create(SpannableTheme, AsyncDrawable.Loader, UrlProcessor, LinkSpan.Resolver)
```

Or, if further tweaking is requered builder methods:
```java
// creates empty builder
SpannableHtmlParser.builder();

// creates builder that is set-up to default values
SpannableHtmlParser.builderWithDefaults(
    @NonNull SpannableTheme theme,
    @Nullable AsyncDrawable.Loader asyncDrawableLoader,
    @Nullable UrlProcessor urlProcessor,
    @Nullable LinkSpan.Resolver resolver
)
```

Builder with defaults additionally handles these HTML tags:
* `b`, `strong`
* `i`, `em`, `cite`, `dfn`
* `sup`
* `sub`
* `u`
* `del`, `s`, `strike`
* `a`
* `img` (only if `AsyncDrawable.Loader` was provided)

You can add own simple tags handling (or override default) via:
```java
SpannableHtmlParser.Builder.simpleTag(String, SpanProvider)
```

Please note, that not all tags are possible to handle via this. These are so called `void` tags ([link](https://www.w3.org/TR/html51/syntax.html#void-elements)) and so-called `html-blocks` ([link](http://spec.commonmark.org/0.18/#html-blocks)). An exception is made only for `img` tag -> it's possible to handle it via `imageProvider` property in `Builder`



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