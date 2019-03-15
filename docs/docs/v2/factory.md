# Factory <Badge text="1.1.0" />

`SpannableFactory` is used to create Span implementations.

```java
SpannableConfiguration.builder(context)
        .factory(SpannableFactory)
        .build();
```

`Markwon` provides default `SpannableFactoryDef` implementation that is
used by default.

Spans:
* `strongEmphasis`
* `emphasis`
* `blockQuote`
* `code`
* `orderedListItem`
* `bulletListItem`
* `thematicBreak`
* `heading`
* `strikethrough`
* `taskListItem`
* `tableRow`
* `paragraph` <Badge text="1.1.1" />
* `image`
* `link`
* `superScript` (HTML content only)
* `subScript` (HTML content only)
* `underline` (HTML content only)

:::tip
`SpannableFactory` can be used to ignore some kinds of text markup. If, for example,
you do not wish to apply _emphasis_ styling to your final result, just return `null`
from `emphasis` factory method:
```java
@Nullable
@Override
public Object emphasis() {
    return null;
}
```
:::

:::tip
All factory methods in `SpannableFactory` return an `Object`, but you can actually
return an **array of Objects** if you wish to apply multiple Spans to a single styling node.
For example, let's make all _emphasis_ also <span :style="{color: '#F00'}">red</span>:

```java
@Nullable
@Override
public Object emphasis() {
    return new Object[] {
            super.emphasis(),
            new ForegroundColorSpan(Color.RED)
    };
}
```
:::