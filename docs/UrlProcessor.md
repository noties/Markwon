# UrlProcessor

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