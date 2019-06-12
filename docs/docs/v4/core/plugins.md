# Plugins <Badge text="3.0.0" />

Since <Badge text="3.0.0" /> `MarkwonPlugin` takes the key role in
processing and rendering markdown. Even **core** functionaly is abstracted
into a `CorePlugin`. So it's still possible to use `Markwon` with a completely
own set of plugins.

To register a plugin `Markwon.Builder` must be used:

```java
Markwon.builder(context)
    // @since 4.0.0 there is no need to register CorePlugin, as it's registered automatically
//    .usePlugin(CorePlugin.create())
    .usePlugin(MyPlugin.create())
    .build();
```

All the process of transforming _raw_ markdown into a styled text (Spanned)
will go through plugins. A plugin can:

* [configure plugin registry](#registry)
* [configure commonmark-java `Parser`](#parser)
* [configure `MarkwonTheme`](#markwontheme)
* [configure `AsyncDrawableLoader` (used to display images in markdown)](#images)
* [configure `MarkwonConfiguration`](#configuration)
* [configure `MarkwonVisitor` (extensible commonmark-java Node visitor)](#visitor)
* [configure `MarkwonSpansFactory` (factory to hold spans information for each Node)](#spans-factory)

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

## Registry <Badge text="4.0.0" />

Registry is a special step to pre-configure all registered plugins. It is also
used to determine the order of plugins inside `Markwon` instance.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configure(@NonNull Registry registry) {

                final CorePlugin corePlugin = registry.require(CorePlugin.class);

                // or
                registry.require(CorePlugin.class, new Action<CorePlugin>() {
                    @Override
                    public void apply(@NonNull CorePlugin corePlugin) {

                    }
                });
            }
        })
        .build();
```

More information about registry can be found [here](/docs/v4/core/registry.md)

## Parser

For example, let's register a new commonmark-java Parser extension:

```java
final Markwon markwon = Markwon.builder(context)
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

:::tip
`CorePlugin` has special handling - it will be added automatically 
when `Markwon.builder(Context)` method is used. If you wish to create 
Markwon instance _without_ CorePlugin registered - 
use `Markwon.builderNoCore(Context)` method instead
:::

More information about `MarkwonTheme` can be found [here](/docs/v4/core/theme.md).


## Configuration

`MarkwonConfiguration` is a set of common tools that are used by different parts
of `Markwon`. It allows configurations of these:

* `AsyncDrawableLoader` (image loading)
* `SyntaxHighlight` (highlighting code blocks)
* `LinkResolver` (opens links in markdown)
* `UrlProcessor` (process URLs in markdown for both links and images)
* `ImageSizeResolver` (resolve image sizes, like `fit-to-canvas`, etc)

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                builder.linkResolver(new LinkResolverDef());
            }
        })
        .build();
```

More information about `MarkwonConfiguration` can be found [here](/docs/v4/core/configuration.md)


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
                // please note that strike-through parser extension must be registered
                // in order to receive such callback
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
you can disable `Heading` Node rendering:

```java
builder.on(Heading.class, null);
```
:::

More information about `MarkwonVisitor` can be found [here](/docs/v4/core/visitor.md)


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

More information about spans factory can be found [here](/docs/v4/core/spans-factory.md)


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

## What happens underneath

Here is what happens inside `Markwon` when `setMarkdown` method is called:

```java
final Markwon markwon = Markwon.create(context);

// warning: pseudo-code

// 0. each plugin will be called to _pre-process_ raw input markdown
rawInput = plugins.reduce(rawInput, (input, plugin) -> plugin.processMarkdown(input));

// 1. after input is processed it's being parsed to a Node
node = parser.parse(rawInput);

// 2. each plugin will be able to inspect or manipulate resulting Node
//  before rendering
plugins.forEach(plugin -> plugin.beforeRender(node));

// 3. node is being visited by a visitor
node.accept(visitor);

// 4. each plugin will be called after node is being visited (aka rendered)
plugins.forEach(plugin -> plugin.afterRender(node, visitor));

// 5. styled markdown ready at this point
final Spanned markdown = visitor.markdown();

// NB, points 6-8 are applied **only** if markdown is set to a TextView

// 6. each plugin will be called before styled markdown is applied to a TextView
plugins.forEach(plugin -> plugin.beforeSetText(textView, markdown));

// 7. markdown is applied to a TextView
textView.setText(markdown);

// 8. each plugin will be called after markdown is applied to a TextView
plugins.forEach(plugin -> plugin.afterSetText(textView));
```