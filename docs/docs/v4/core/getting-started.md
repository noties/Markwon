# Getting started

:::tip Installation
Please follow [installation](/docs/v4/install.md) instructions
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
