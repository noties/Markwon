# HTML

<MavenBadge4 :artifact="'html'" />

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

---

Staring with <Badge text="4.0.0" /> you can exclude all default tag handlers:

```java
.usePlugin(HtmlPlugin.create(new HtmlPlugin.HtmlConfigure() {
    @Override
    public void configureHtml(@NonNull HtmlPlugin plugin) {
        plugin.excludeDefaults(true);
    }
}))
```

or via plugin:

```java
.usePlugin(new AbstractMarkwonPlugin() {
    @Override
    public void configure(@NonNull Registry registry) {
        registry.require(HtmlPlugin.class, new Action<HtmlPlugin>() {
            @Override
            public void apply(@NonNull HtmlPlugin htmlPlugin) {
                htmlPlugin.excludeDefaults(true);
            }
        });
    }
})
```

If you wish to exclude some of them `TagHandlerNoOp` can be used:

```java
.usePlugin(HtmlPlugin.create(new HtmlPlugin.HtmlConfigure() {
    @Override
    public void configureHtml(@NonNull HtmlPlugin plugin) {
        plugin.addHandler(TagHandlerNoOp.create("h4", "h5", "h6", "img"));
    }
}))
```

## TagHandler

To define a tag-handler that applies style for the whole tag content (from start to end),
a `SimpleTagHandler` can be used. For example, let's define `<align>` tag, which can be used
like this:

* `<align center>centered text</align>`
* `<align end>this should be aligned at the end (right for LTR locales)</align>`
* `<align>regular alignment</align>`

```java
public class AlignTagHandler extends SimpleTagHandler {

    @Nullable
    @Override
    public Object getSpans(
            @NonNull MarkwonConfiguration configuration,
            @NonNull RenderProps renderProps,
            @NonNull HtmlTag tag) {

        final Layout.Alignment alignment;

        // html attribute without value, <align center></align>
        if (tag.attributes().containsKey("center")) {
            alignment = Layout.Alignment.ALIGN_CENTER;
        } else if (tag.attributes().containsKey("end")) {
            alignment = Layout.Alignment.ALIGN_OPPOSITE;
        } else {
            // empty value or any other will make regular alignment
            alignment = Layout.Alignment.ALIGN_NORMAL;
        }

        return new AlignmentSpan.Standard(alignment);
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Collections.singleton("align");
    }
}
```

:::tip
`SimpleTagHandler` can return an array of spans from `getSpans` method
:::

Then register `AlignTagHandler`:

```java
final Markwon markwon = Markwon.builder(this)
        .usePlugin(HtmlPlugin.create())
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configure(@NonNull Registry registry) {
                registry.require(HtmlPlugin.class, htmlPlugin -> htmlPlugin
                        .addHandler(new AlignTagHandler());
            }
        })
        .build();
```

or directly on `HtmlPlugin`:

```java
final Markwon markwon = Markwon.builder(this)
        .usePlugin(HtmlPlugin.create(plugin -> plugin.addHandler(new AlignTagHandler())))
        .build();
```

---

If a tag requires special handling `TagHandler` can be used directly. For example
let's define an `<enhance>` tag with `start` and `end` arguments, that will mark
start and end positions of the text that needs to be enlarged:

```html
<enhance start="5" end="12">This is text that must be enhanced, at least a part of it</enhance>
```


```java
public class EnhanceTagHandler extends TagHandler {

    private final int enhanceTextSize;

    EnhanceTagHandler(@Px int enhanceTextSize) {
        this.enhanceTextSize = enhanceTextSize;
    }

    @Override
    public void handle(
            @NonNull MarkwonVisitor visitor,
            @NonNull MarkwonHtmlRenderer renderer,
            @NonNull HtmlTag tag) {

        // we require start and end to be present
        final int start = parsePosition(tag.attributes().get("start"));
        final int end = parsePosition(tag.attributes().get("end"));

        if (start > -1 && end > -1) {
            visitor.builder().setSpan(
                    new AbsoluteSizeSpan(enhanceTextSize),
                    tag.start() + start,
                    tag.start() + end
            );
        }
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Collections.singleton("enhance");
    }

    private static int parsePosition(@Nullable String value) {
        int position;
        if (!TextUtils.isEmpty(value)) {
            try {
                position = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                position = -1;
            }
        } else {
            position = -1;
        }
        return position;
    }
}
```