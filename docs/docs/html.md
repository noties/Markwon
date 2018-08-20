# HTML
<Badge text="2.x.x" />

Starting with version `2.x.x` `Markwon` brings the whole HTML parsing/rendering
stack _on-site_. The main reason for this are _special_ definitions of HTML nodes
by <Link name="commonmark-spec" />. More specifically: <Link name="commonmark-spec#inline" displayName="inline" /> 
and <Link name="commonmark-spec#block" displayName="block" />.
These two are _a bit_ different from _native_ HTML understanding.
Well, they are completely different and share only the same names as
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

Because of this I 

afterall creating an implementation of a web browser is not a purpose of this library.

html-entities

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