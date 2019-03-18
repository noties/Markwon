# Getting started

## Quick one

This is the most simple way to set markdown to a `TextView` or any of its siblings:

```java
Markwon.setMarkdown(textView, "**Hello there!**");
```

The most simple way to obtain markdown to be applied _somewhere_ else:

```java
// parsed and styled markdown
final CharSequence markdown = Markwon.markdown(context, "**Hello there!**");

// use it
Toast.makeText(context, markdown, Toast.LENGTH_LONG).show();
```

## Longer one

When you need to customize markdown parsing/rendering you can use [SpannableConfiguration](/docs/v2/configure.md):

```java
final SpannableConfiguration configuration = SpannableConfiguration.builder(context)
        .asyncDrawableLoader(AsyncDrawableLoader.create())
        .build();

Markwon.setMarkdown(textView, configuration, "Are **you** still there?");

final CharSequence markdown = Markwon.markdown(configuration, "Are **you** still there?");
Toast.makeText(context, markdown, Toast.LENGTH_LONG).show();
```

## No magic one

In order to understand how previous examples work, let's break them down:

* construct a `Parser` (see: <Link name="commonmark-java" />) and parse markdown
* construct a `SpannableConfiguration` (if it's not provided)
* *render* parsed markdown to Spannable (via `SpannableRenderer`)
* prepares TextView to display images, tables and links
* sets text

This flow answers the most simple usage of displaying markdown: one shot parsing
&amp; configuration of relatively small markdown chunks. If your markdown contains
a lot of text or you plan to display multiple UI widgets with markdown you might 
consider *stepping in* and taking control of this flow.

The candidate requirements to *step in*:
* parsing and processing of parsed markdown in a background thread
* reusing `Parser` and/or `SpannableConfiguration` between multiple calls
* ignore images or tables specific logic (you know that markdown won't contain them)

So, if we expand `Markwon.setMarkdown(textView, markdown)` method we will see the following:

```java
// create a Parser instance (can be done manually)
// internally creates default Parser instance & registers `strike-through` & `tables` extension
final Parser parser = Markwon.createParser();

// core class to display markdown, can be obtained via this method,
// which creates default instance (no images handling though),
// or via `builder` method, which lets you to configure this instance
final SpannableConfiguration configuration = SpannableConfiguration.create(context);

final SpannableRenderer renderer = new SpannableRenderer();

final Node node = parser.parse(markdown);
final CharSequence text = renderer.render(configuration, node);

// for links in markdown to be clickable
textView.setMovementMethod(LinkMovementMethod.getInstance());

// we need these due to the limited nature of Spannables to invalidate TextView
Markwon.unscheduleDrawables(textView);
Markwon.unscheduleTableRows(textView);

// @since 2.0.1 we must measure ordered list items _before_ they are rendered
OrderedListItemSpan.measure(view, text);

textView.setText(text);

Markwon.scheduleDrawables(textView);
Markwon.scheduleTableRows(textView);
```

:::tip Note
If you are having trouble with `LinkMovementMethod` you can use
`Markwon.setText(textView, markdown, movementMethod)` method <Badge text="1.0.6" /> to specify _no_ movement
method (aka `null`) or own implementation. As an alternative to the system `LinkMovementMethod`
you can use [Better-Link-Movement-Method](https://github.com/saket/Better-Link-Movement-Method).
Please note that `Markwon.setText` method expects _parsed_ markdown as the second argument.
:::