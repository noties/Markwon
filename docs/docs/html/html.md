---
title: 'Overview'
---

# HTML <Badge text="2.0.0" />

<MavenBadge :artifact="'html'" />

Starting with version `2.0.0` `Markwon` brings the whole HTML parsing/rendering
stack _on-site_. The main reason for this are _special_ definitions of HTML nodes
by <Link name="commonmark-spec" />. More specifically: <Link name="commonmark-spec#inline" displayName="inline" /> 
and <Link name="commonmark-spec#block" displayName="block" />.
These two are _a bit_ different from _native_ HTML understanding.
Well, they are _completely_ different and share only the same names as
<Link name="html-inlines" displayName="HTML-inline"/> and <Link name="html-blocks" displayName="HTML-block"/>
elements. This leads to situations when for example an `<i>` tag is considered
a block when it's used like this:

```markdown
<i>
Hello from italics tag
</i>
```

:::tip A bit of background
<br>
<GithubIssue id="52" displayName="This issue" /> had brought attention to differences between HTML &amp; commonmark implementations. <br><br>
:::

Let's modify code snippet above _a bit_:

```markdown{3}
<i>
Hello from italics tag

</i>
```

We have just added a `new-line` before closing `</i>` tag. And this
changes everything as now, according to the <Link name="commonmark-dingus" />,
we have 2 HtmlBlocks: one before `new-line` (containing open `<i>` tag and text content)
and one after (containing as little as closing `</i>` tag).

If we modify code snippet _a bit_ again:

```markdown{4}
<i>
Hello from italics tag

</i><b>bold></b>
```

We will have 1 HtmlBlock (from previous snippet) and a bunch of HtmlInlines:
* HtmlInline (`<i>`)
* HtmlInline (`<b>`)
* Text (`bold`)
* HtmlInline (`</b>`)

Those _little_ differences render `Html.fromHtml` (which was used in `1.x.x` versions)
useless. And actually it renders most of the HTML parsers implementations useless,
as most of them do not allow processing of HTML fragments in a raw fashion
without _fixing_ content on-the-fly.

Both `TagSoup` and `Jsoup` HTML parsers (that were considered for this project) are built to deal with 
_malicious_ HTML code (*all HTML code*? :no_mouth:). So, when supplied 
with a `<i>italic` fragment they will make it `<i>italic</i>`.
And it's a good thing, but consider these fragments for the sake of markdown:

* `<i>italic `
* `<b>bold italic`
* `</b><i>`

We will get:

* `<i>italic </i>`
* `<b>bold italic</b>`

_<sup>*</sup> Or to be precise: `<html><head></head><body><i>italic </i></body></html>` &amp;
`<html><head></head><body><b>bold italic</b></body></html>`_

Which will be rendered in a final document:


|expected|actual|
|---|---|
|<i>italic <b>bold italic</b></i>|<i>italic </i><b>bold italic</b>|

This might seem like a minor problem, but add more tags to a document,
introduce some deeply nested structures, spice openning and closing tags up
by adding markdown markup between them and finally write _malicious_ HTML code :laughing:!

There is no such problem on the _frontend_ for which commonmark specification is mostly
aimed as _frontend_ runs in a web-browser environment. After all _parsed_ markdown
will become HTML tags (most common usage). And web-browser will know how to render final result.

We, on the other hand, do not posess HTML heritage (*thank :robot:!*), but still
want to display some HTML to style resulting markdown a bit. That's why `Markwon`
incorporated own HTML parsing logic. It is based on the <Link name="jsoup" /> project.
And makes usage of the `Tokekiser` class that allows to _tokenise_ input HTML.
All other code that doesn't follow this purpose was removed. It's safe to use
in projects that already have `jsoup` dependency as `Markwon` repackaged **jsoup** source classes
(which could be found <Link name="markwon-jsoup" displayName="here"/>)

## Parser

There are no additional steps to configure HTML parsing. It's enabled by default.
If you wish to _exclude_ it, please follow the [exclude](#exclude-html-parsing) section below.

The key class here is: `MarkwonHtmlParser` that is defined in `markwon-html-parser-api` module.
`markwon-html-parser-api` is a simple module that defines HTML parsing contract and
does not provide implementation. 

To change what implementation `Markwon` should use, `SpannableConfiguration` can be used:

```java{2}
SpannableConfiguration.builder(context)
        .htmlParser(MarkwonHtmlParser)
        .build();
```

`markwon-html-parser-impl` on the other hand provides `MarkwonHtmlParser` implementation.
It's called `MarkwonHtmlParserImpl`. It can be created like this:

```java
final MarkwonHtmlParser htmlParser = MarkwonHtmlParserImpl.create();
// or
final MarkwonHtmlParser htmlParser = MarkwonHtmlParserImpl.create(HtmlEmptyTagReplacement);
```

### Empty tag replacement

In order to append text content for self-closing, void or just _empty_ HTML tags,
`HtmlEmptyTagReplacement` can be used. As we cannot set Span for empty content,
we must represent empty tag with text during parsing stage (if we want it to be represented).

Consider this:
* `<img src="me-sad.JPG">`
* `<br />`
* `<who-am-i></who-am-i>`

By default (`HtmlEmptyTagReplacement.create()`) will handle `img` and `br` tags.
`img` will be replaced with `alt` property if it is present and `\uFFFC` if it is not. 
And `br` will insert a new line.

### Non-closed tags

It's possible that your HTML can contain non-closed tags. By default `Markwon` will ignore them,
but if you wish to get a bit closer to a web-browser experience, you can allow this behaviour:

```java{2}
SpannableConfiguration.builder(context)
        .htmlAllowNonClosedTags(true)
        .build();
```

:::warning Note
If there is (for example) an `<i>` tag at the start of a document and it's not closed
and `Markwon` is configured to **not** ignore non-closed tags (`.htmlAllowNonClosedTags(true)`),
it will make the whole document in italics
:::

### Implementation note

`MarkwonHtmlParserImpl` does not create a unified HTML node. Instead it creates
2 collections: inline tags and block tags. Inline tags are represented as a `List`
of inline tags (<Link name="html-inlines" displayName="reference" />). And
block tags are structured in a tree. This helps to achieve _browser_-like behaviour,
when open inline tag is applied to all content (even if inside blocks) until closing tag.
All tags that are not _inline_ are considered to be _block_ ones.

## Renderer

Unlike `MarkwonHtmlParser` `Markwon` comes with a `MarkwonHtmlRenderer` by default.

Default implementation can be obtain like this:

```java
MarkwonHtmlRenderer.create();
```

Default instance have these tags _handled_:
* emphasis
  * `i`
  * `em`
  * `cite`
  * `dfn`
* strong emphasis
  * `b`
  * `strong`
* `sup` (super script)
* `sub` (sub script)
* underline
  * `u`
  * `ins`
* strike through
  * `del`
  * `s`
  * `strike`
* `a` (link)
* `ul` (unordered list)
* `ol` (ordered list)
* `img` (image)
* `blockquote` (block quote)
* `h{1-6}` (heading)

If you wish to _extend_ default handling (or override existing),
`#builderWithDefaults` factory method can be used:

```java
MarkwonHtmlRenderer.builderWithDefaults();
```

For a completely _clean_ configurable instance `#builder` method can be used:

```java
MarkwonHtmlRenderer.builder();
```

### Custom tag handler

To configure `MarkwonHtmlRenderer` to handle tags differently or 
create a new tag handler - `TagHandler` can be used

```java
public abstract class TagHandler {

    public abstract void handle(
            @NonNull SpannableConfiguration configuration,
            @NonNull SpannableBuilder builder,
            @NonNull HtmlTag tag
    );
}
```

For the most simple _inline_ tag handler a `SimpleTagHandler` can be used:

```java
public abstract class SimpleTagHandler extends TagHandler {

    @Nullable
    public abstract Object getSpans(@NonNull SpannableConfiguration configuration, @NonNull HtmlTag tag);
}
```

For example, `EmphasisHandler`:

```java
public class EmphasisHandler extends SimpleTagHandler {
    @Nullable
    @Override
    public Object getSpans(@NonNull SpannableConfiguration configuration, @NonNull HtmlTag tag) {
        return configuration.factory().emphasis();
    }
}
```

If you wish to handle a _block_ HTML node (for example `<ul><li>First<li>Second</ul>`) refer
to `ListHandler` source code for reference.

:::warning
The most important thing when implementing custom `TagHandler` is to know
what type of `HtmlTag` we are dealing with. There are 2: inline &amp; block.
Inline tag cannot contain children. Block _can_ contain children. And they
_most likely_ should also be visited and _handled_ by registered `TagHandler` (if any)
accordingly. See `TagHandler#visitChildren(configuration, builder, child);`
:::

#### Css inline style parser

When implementing own `TagHandler` you might want to inspect inline CSS styles
of a HTML element. `Markwon` provides an utility parser for that purpose:

```java
final CssInlineStyleParser inlineStyleParser = CssInlineStyleParser.create();
for (CssProperty property: inlineStyleParser.parse("width: 100%; height: 100%;")) {
    // [0] = CssProperty({width=100%}),
    // [1] = CssProperty({height=100%})
}
```

## Exclude HTML parsing

If you wish to exclude HTML parsing altogether, you can manually
exclude `markwon-html-parser-impl` artifact from your projects compile classpath.
This can be beneficial if you know that markdown input won't contain
HTML and/or you wish to ignore it. Excluding HTML parsing
can speed up `Markwon` parsing and will decrease final size of
`Markwon` dependency by around `100kb`.

<MavenBadge :artifact="'markwon'" />

```groovy
dependencies {
    implementation("ru.noties:markwon:${markwonVersion}") {
        exclude module: 'markwon-html-parser-impl'
    }
}
```

Excluding `markwon-html-parser-impl` this way will result in
`MarkwonHtmlParser#noOp` implementation. No further steps are 
required.

:::warning Note
Excluding `markwon-html-parser-impl` won't remove *all* the content between
HTML tags. It will if `commonmark` decides that a specific fragment is a 
`HtmlBlock`, but it won't if fragment is considered a `HtmlInline` as `HtmlInline`
does not contain content (just a tag definition).
:::