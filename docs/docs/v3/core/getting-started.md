# Getting started

<LegacyWarning />

:::tip Installation
Please follow [installation](/docs/v3/install.md) instructions
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
refer to [migration](/docs/v3/migration-2-3.md) section.
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

This section is kept due to historical reasons. Starting with version <Badge text="3.0.0" />
the amount of magic is reduced. To leverage your `Markwon` usage a concept of `Plugin`
is introduced which helps to extend default behavior in a simple and _no-breaking-the-flow_ manner.
Head to the [next section](/docs/v3/core/plugins.md) to know more.
