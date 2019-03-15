# HTML

This artifact encapsulates HTML parsing from the core artifact and provides
few predefined `TagHandlers`

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(HtmlPlugin.create())
        .build();
```

As this artifact brings modified [jsoup](https://github.com/jhy/jsoup) library 
it was moved to a standalone module in order to minimize dependencies and unused code
in applications that does not require HTML render capabilities.

Before <Badge text="2.0.0" /> `Markwon` used android `Html` class for parsing and
rendering. Unfortunately, according to markdown specification, markdown can contain
HTML in _unpredictable_ way if rendered _outside_ of browser. For example:

```markdown{4}
<i>
Hello from italics tag

</i><b>bold></b>
```

This snippet could be represented as:
* HtmlBlock (`<i>\nHello from italics tag`)
* HtmlInline (`<i>`)
* HtmlInline (`<b>`)
* Text (`bold`)
* HtmlInline (`</b>`)

:::tip A bit of background
<br>
<GithubIssue id="52" displayName="This issue" /> had brought attention to differences between HTML &amp; commonmark implementations. <br><br>
:::

Unfortunately Android `HTML` class cannot parse a _fragment_ of HTML to later
be included in a bigger set of content. This is why the decision was made to bring
HTML parsing _in-markwon-house_

## Predefined TagHandlers
* `<img>`
* `<a>`
* `<blockquote>`
* `<sub>`
* `<sup>`
* `<b>, <strong>`
* `<s>, <del>`
* `<u>, <ins>`
* `<ul>, <ol>`
* `<i>, <cite>, <em>, <dfn>`
* `<h1>, <h2>, <h3>, <h4>, <h5>, <h6>`

:::tip
All predefined tag handlers will use styling spans for native markdown content.
So, if your `Markwon` instance was configured to, for example, render Emphasis
nodes as a <span style="color: #FF0000">red text</span> then HTML tag handler will
use the same span. This includes images, links, UrlResolver, LinkProcessor, etc
:::

To learn more about defining own TagHandlers, please refer to [html-renderer docs](/docs/v3/core/html-renderer.md)
