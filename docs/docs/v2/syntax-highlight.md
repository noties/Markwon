# Syntax highlight

<LegacyWarning />

<MavenBadge2xx artifact="markwon-syntax-highlight" />

This is a simple module to add **syntax highlight** functionality to your markdown rendered with `Markwon` library. It is based on [Prism4j](https://github.com/noties/Prism4j) so lead there to understand how to configure `Prism4j` instance.

<img :src="$withBase('/art/markwon-syntax-default.png')" alt="theme-default" width="80%">


<img :src="$withBase('/art/markwon-syntax-darkula.png')" alt="theme-darkula" width="80%">

---

First, we need to obtain an instance of `Prism4jSyntaxHighlight` which implements Markwon's `SyntaxHighlight`:

```java
final SyntaxHighlight highlight = 
    Prism4jSyntaxHighlight.create(Prism4j, Prism4jTheme);
```

we also can obtain an instance of `Prism4jSyntaxHighlight` that has a _fallback_ option (if a language is not defined in `Prism4j` instance, fallback language can be used):

```java
final SyntaxHighlight highlight = 
    Prism4jSyntaxHighlight.create(Prism4j, Prism4jTheme, String);
```

Generally obtaining a `Prism4j` instance is pretty easy:

```java
final Prism4j prism4j = new Prism4j(new GrammarLocatorDef());
```

Where `GrammarLocatorDef` is a generated grammar locator (if you use `prism4j-bundler` annotation processor)

`Prism4jTheme` is a specific type that is defined in this module (`prism4j` doesn't know anything about rendering). It has 2 implementations:

* `Prism4jThemeDefault`
* `Prism4jThemeDarkula`

Both of them can be obtained via factory method `create`:

* `Prism4jThemeDefault.create()`
* `Prism4jThemeDarkula.create()`

But of cause nothing is stopping you from defining your own theme:

```java
public interface Prism4jTheme {

    @ColorInt
    int background();

    @ColorInt
    int textColor();

    void apply(
            @NonNull String language,
            @NonNull Prism4j.Syntax syntax,
            @NonNull SpannableStringBuilder builder,
            int start,
            int end
    );
}
```

:::tip
You can extend `Prism4jThemeBase` which has some helper methods
:::