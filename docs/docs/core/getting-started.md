# Getting started

:::tip Installation
Please follow [installation](/docs/install.md) instructions
to learn how to add `Markwon` to your project
:::

## Quick one

This is the most simple way to set markdown to a `TextView` or any of its siblings:

```java
// obtain an instance of Markwon
final Markwon markwon = Markwon.create(context);

// set markdown
markwon.setMarkdown(textView, "**Hello there!**");
```

The most simple way to obtain markdown to be applied _somewhere_ else:

```java
// obtain an instance of Markwon
final Markwon markwon = Markwon.create(context);

// parse markdown and create styled text
final Spanned markdown = markwon.toMarkdown("**Hello there!**");

// use it
Toast.makeText(context, markdown, Toast.LENGTH_LONG).show();
```

:::warning 3.x.x migration
Starting with <Badge text="3.0.0" /> version Markwon no longer relies on static
utility methods. To learn more about migrating existing applications
refer to [migration](/docs/migration-2-3.md) section.
:::

## Longer one

With explicit `parse` and `render` methods:

```java
// obtain an instance of Markwon
final Markwon markwon = Markwon.create(context);

// parse markdown to commonmark-java Node
final Node node = markwon.parse("Are **you** still there?");

// create styled text from parsed Node
final Spanned markdown = markwon.render(node);

// use it on a TextView
markwon.setParsedMarkdown(textView, markdown);

// or a Toast
Toast.makeText(context, markdown, Toast.LENGTH_LONG).show();
```

## No magic one

So, what happens _internally_ when there is a `markwon#setMarkdown(TextView,String)` call?
Please note that this is mere representaion of what happens underneath and a caller
would likely never has to deal with these method calls directly. It still valuable
to understand how things are working:

```java
// `Markwon#create` implicitly uses CorePlugin
final Markwon markwon = Markwon.builder(context)
        .usePlugin(CorePlugin.create())
        .build();

// each plugin will configure resulting Markwon instance
// we will cover it in plugins section of documentation

// warning: pseudo-code

// 0. each plugin will be called to _pre-process_ raw input markdown
rawInput = plugins.reduce(rawInput, (input, plugin) -> plugin.processMarkdown(input));

// 1. after input is processed it's being parsed to a Node
node = parser.parse(rawInput);

// 2. each plugin will configure RenderProps
plugins.forEach(plugin -> plugin.configureRenderProps(renderProps));

// 3. each plugin will be able to inspect or manipulate resulting Node
//  before rendering
plugins.forEach(plugin -> plugin.beforeRender(node));

// 4. node is being visited by a visitor
node.accept(visitor);

// 5. each plugin will be called after node is being visited (aka rendered)
plugins.forEach(plugin -> plugin.afterRender(node, visitor));

// 6. styled markdown ready at this point
final Spanned markdown = visitor.markdown();

// 7. each plugin will be called before styled markdown is applied to a TextView
plugins.forEach(plugin -> plugin.beforeSetText(textView, markdown));

// 8. markdown is applied to a TextView
textView.setText(markdown);

// 9. each plugin will be called after markdown is applied to a TextView
plugins.forEach(plugin -> plugin.afterSetText(textView));
```

As you can see a `plugin` is what lifts the most weight. We will cover
plugins next.

:::tip Note
If you are having trouble with `LinkMovementMethod` you can use
`Markwon.setText(textView, markdown, movementMethod)` method <Badge text="1.0.6" /> to specify _no_ movement
method (aka `null`) or own implementation. As an alternative to the system `LinkMovementMethod`
you can use [Better-Link-Movement-Method](https://github.com/saket/Better-Link-Movement-Method).
Please note that `Markwon.setText` method expects _parsed_ markdown as the second argument.
:::