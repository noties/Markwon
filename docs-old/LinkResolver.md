# LinkResolver

Link resolver is used to navigate to clicked link. By default `LinkResolverDef` is used and it just constructs an `Intent` and launches activity that can handle it, or silently fails if activity cannot be resolved. The main interface:
```java
public interface Resolver {
    void resolve(View view, @NonNull String link);
}
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