# Plugins <Badge text="3.0.0" />

Since <Badge text="3.0.0" /> `MarkwonPlugin` takes the key role in
processing and rendering markdown. Even **core** functionaly is abstracted
into a `CorePlugin`. So it's still possible to use `Markwon` with a completely
own set of plugins.

To register a plugin `Markwon.Builder` must be used:

```java
Markwon.builder(context)
    .usePlugin(CorePlugin.create())
    .build();
```

All the process of transforming _raw_ markdown into a styled text (Spanned)
will go through plugins. A plugin can:

* [configure commonmark-java `Parser`](#parser)
* [configure `MarkwonTheme`](#markwontheme)
* [configure `AsyncDrawableLoader` (used to display images in markdown)](#images)
* [configure `MarkwonConfiguration`](#configuration)
* [configure `MarkwonVisitor` (extensible commonmark-java Node visitor)](#visitor)
* [configure `MarkwonSpansFactory` (factory to hold spans information for each Node)](#spans-factory)
* [configure `MarkwonHtmlRenderer` (utility to properly display HTML in markdown)](#html-renderer)

---

* [declare a dependency on another plugin (will be used as a runtime validator)](#priority)

---

* [process raw input markdown before parsing it](#process-markdown)
* [inspect/modify commonmark-java Node after it's been parsed, but before rendering](#inspect-modify-node)
* [inspect commonmark-java Node after it's been rendered](#inspect-node-after-render)
* [prepare TextView to display markdown _before_ markdown is applied to a TextView](#prepare-textview)
* [post-process TextView _after_ markdown was applied](#textview-after-markdown-applied)

:::tip
if you need to override only few methods of `MarkwonPlugin` (since it is an interface),
`AbstractMarkwonPlugin` can be used.
:::

## Parser

For example, let's register a new commonmark-java Parser extension:

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(CorePlugin.create())
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureParser(@NonNull Parser.Builder builder) {
                // no need to call `super.configureParser(builder)`
                builder.extensions(Collections.singleton(StrikethroughExtension.create()));
            }
        })
        .build();
```

There are no limitations on what to do with commonmark-java Parser. For more info
_what_ can be done please refer to <Link name="commonmark-java" displayName="commonmark-java documentation" />.

## MarkwonTheme

Starting <Badge text="3.0.0" /> `MarkwonTheme` represents _core_ theme. Aka theme for
things core module knows of. For example it doesn't know anything about `strikethrough`
or `tables` (as they belong to different modules).

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                builder
                        .codeTextColor(Color.BLACK)
                        .codeBackgroundColor(Color.GREEN);
            }
        })
        .build();
```

:::warning
`CorePlugin` has special handling - it will be **implicitly** added
if a plugin declares dependency on it. This is why in previous example we haven't
added CorePlugin _explicitly_ as `AbstractMarkwonPlugin` declares a dependency on it.
If it's not desireable override `AbstractMarkwonPlugin#priority` method to specify own rules.
:::

More information about `MarkwonTheme` can be found [here](/docs/core/theme.md).


## Images

Since <Badge text="3.0.0" /> core images functionality moved to the `core` module.
Now `Markwon` comes bundled with support for regular images (no `SVG` or `GIF`, they
defined in standalone modules now). And 3(4) schemes supported by default:
* http (+https; using system built-in `HttpURLConnection`)
* file (including Android assets)
* data (image inline, `data:image/svg+xml;base64,!@#$%^&*(`)

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create())
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {
                // sorry, these are not bundled with the library
                builder
                        .addSchemeHandler("ftp", new FtpSchemeHandler("root", ""))
                        .addMediaDecoder("text/plain", new AnsiiMediaDecoder());
            }
        })
        .build();
```

:::warning
Although `ImagesPlugin` is bundled with the `core` artifact, it is **not** used by default
and one must **explicitly** add it:

```java
Markwon.builder(context)
        .usePlugin(ImagesPlugin.create(context));
```

Without explicit usage of `ImagesPlugin` all image configuration will be ignored (no-op'ed)
:::

More information about dealing with images can be found [here](/docs/core/images.md)


## Configuration

`MarkwonConfiguration` is a set of common tools that are used by different parts
of `Markwon`. It allows configurations of these:

* `SyntaxHighlight` (highlighting code blocks)
* `LinkResolver` (opens links in markdown)
* `UrlProcessor` (process URLs in markdown for both links and images)
* `MarkwonHtmlParser` (HTML parser)
* `ImageSizeResolver` (resolve image sizes, like `fit-to-canvas`, etc)

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                // MarkwonHtmlParserImpl is defined in `markwon-html` artifact
                builder.htmlParser(MarkwonHtmlParserImpl.create());
            }
        })
        .build();
```

More information about `MarkwonConfiguration` can be found [here](/docs/core/configuration.md)


## Visitor

`MarkwonVisitor` <Badge text="3.0.0" /> is commonmark-java Visitor that allows
configuration of how each Node is visited. There is no longer need to create
own subclass of Visitor and override required methods (like in `2.x.x` versions).
`MarkwonVisitor` also allows registration of Nodes, that `core` module knows
nothing about (instead of relying on `visit(CustomNode)` method)).

For example, let's add `strikethrough` Node visitor:

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                builder
                        .on(Strikethrough.class, new MarkwonVisitor.NodeVisitor<Strikethrough>() {
                            @Override
                            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Strikethrough strikethrough) {
                                final int length = visitor.length();
                                visitor.visitChildren(strikethrough);
                                visitor.setSpansForNodeOptional(strikethrough, length);
                            }
                        });
            }
        })
        .build();
```

:::tip
`MarkwonVisitor` also allows _overriding_ already registered nodes. For example,
we can disable `Heading` Node rendering:

```java
builder.on(Heading.class, null);
```

Please note that `Priority` plays nicely here to ensure that your
custom Node override/disable happens _after_ some plugin defines it.
:::

More information about `MarkwonVisitor` can be found [here](/docs/core/visitor.md)


## Spans Factory

`MarkwonSpansFactory` <Badge text="3.0.0" /> is an abstract factory (factory that produces other factories)
for spans that `Markwon` uses. It controls what spans to use for certain Nodes.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                // override emphasis factory to make all emphasis nodes underlined
                builder.setFactory(Emphasis.class, new SpanFactory() {
                    @Override
                    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                        return new UnderlineSpan();
                    }
                });
            }
        })
        .build();
```

:::tip
`SpanFactory` allows to return an _array_ of spans to apply multiple spans
for a Node:

```java
@Override
public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
    // make underlined and set text color to red
    return new Object[]{
            new UnderlineSpan(),
            new ForegroundColorSpan(Color.RED)
    };
}
```
:::

More information about spans factory can be found [here](/docs/core/spans-factory.md)


## HTML Renderer

`MarkwonHtmlRenderer` controls how HTML is rendered in markdown.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(HtmlPlugin.create())
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureHtmlRenderer(@NonNull MarkwonHtmlRenderer.Builder builder) {
                // <center> tag handling (deprecated but valid in our case)
                // can be any tag name, there is no connection with _real_ HTML tags,
                // <just-try-to-not-go-crazy-and-remember-about-portability>
                builder.addHandler("center", new SimpleTagHandler() {
                    @Override
                    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps renderProps, @NonNull HtmlTag tag) {
                        return new AlignmentSpan() {
                            @Override
                            public Layout.Alignment getAlignment() {
                                return Layout.Alignment.ALIGN_CENTER;
                            }
                        };
                    }
                });
            }
        })
        .build();
```

:::danger
Although `MarkwonHtmlRenderer` is bundled with `core` artifact, actual
HTML parser is placed in a standalone artifact and must be added to your
project **explicitly** and then registered via `Markwon.Builder#usePlugin(HtmlPlugin.create())`.
If not done so, no HTML will be parsed nor rendered.
:::

More information about HTML rendering can be found [here](/docs/core/html-renderer.md)


## Priority

`Priority` is an abstraction to _state_ dependency connection between plugins. It is
also used as a runtime graph validator. If a plugin defines a dependency on other, but
_other_ is not in resulting `Markwon` instance, then a runtime exception will be thrown.
`Priority` is also defines the order in which plugins will be placed. So, if a plugin `A`
states a plugin `B` as a dependency, then plugin `A` will come **after** plugin `B`.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public Priority priority() {
                return Priority.after(CorePlugin.class);
            }
        })
        .build();
```

:::warning
Please note that `AbstractMarkwonPlugin` _implicitly_ defines `CorePlugin`
as a dependency (`return Priority.after(CorePlugin.class);`). This will
also add `CorePlugin` to a `Markwon` instance, because it will be added
_implicitly_ if a plugin defines it as a dependency. 
:::

Use one of the factory methods to create a `Priority` instance:

```java
// none
Priority.none();

// single dependency
Priority.after(CorePlugin.class);

// 2 dependencies
Priority.after(CorePlugin.class, ImagesPlugin.class);

// for a number >2, use #builder
Priority.builder()
        .after(CorePlugin.class)
        .after(ImagesPlugin.class)
        .after(StrikethroughPlugin.class)
        .build();
```

## Process markdown

A plugin can be used to _pre-process_ input markdown (this will be called before _parsing_):

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @NonNull
            @Override
            public String processMarkdown(@NonNull String markdown) {
                return markdown.replaceAll("foo", "bar");
            }
        })
        .build();
```

## Inspect/modify Node

A plugin can inspect/modify commonmark-java Node _before_ it's being rendered.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void beforeRender(@NonNull Node node) {

                // for example inspect it with custom visitor
                node.accept(new MyVisitor());

                // or modify (you know what you are doing, right?)
                node.appendChild(new Text("Appended"));
            }
        })
        .build();
```

## Inspect Node after render

A plugin can inspect commonmark-java Node after it's been rendered.
Modifying Node at this point makes not much sense (it's already been
rendered and all modifications won't change anything). But this method can be used,
for example, to clean-up some internal state (after rendering). Generally
speaking, a plugin must be stateless, but if it cannot, then this method is
the best place to clean-up.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void afterRender(@NonNull Node node, @NonNull MarkwonVisitor visitor) {
                cleanUp();
            }
        })
        .build();
```

## Prepare TextView

A plugin can _prepare_ a TextView before markdown is applied. For example `images`
unschedules all previously scheduled `AsyncDrawableSpans` (if any) here. This way
when new markdown (and set of Spannables) arrives, previous set won't be kept in
memory and could be garbage-collected.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
                // clean-up previous
                AsyncDrawableScheduler.unschedule(textView);
            }
        })
        .build();
```

## TextView after markdown applied

A plugin will receive a callback _after_ markdown is applied to a TextView.
For example `images` uses this callback to schedule new set of Spannables.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void afterSetText(@NonNull TextView textView) {
                AsyncDrawableScheduler.schedule(textView);
            }
        })
        .build();
```

:::tip
Please note that unlike `#beforeSetText`, `#afterSetText` won't receive
`Spanned` markdown. This happens because at this point spans must be
queried directly from a TextView.
:::