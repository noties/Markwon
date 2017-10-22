![logo](./art/markwon_logo.png)

# Markwon

[![markwon](https://img.shields.io/maven-central/v/ru.noties/markwon.svg?label=markwon)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%markwon%22)
[![markwon-image-loader](https://img.shields.io/maven-central/v/ru.noties/markwon-image-loader.svg?label=markwon-image-loader)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%markwon-image-loader%22)
[![markwon-view](https://img.shields.io/maven-central/v/ru.noties/markwon-view.svg?label=markwon-view)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%markwon-view%22)

- [ ] one **one** _one_
- [X] ~~two~~

**Markwon** is a library for Android that renders markdown as system-native Spannables. It gives ability to display markdown in all TextView widgets (**TextView**, **Button**, **Switch**, **CheckBox**, etc), **Notifications**, **Toasts**, etc. <u>**No WebView is required**</u>. Library provides reasonable defaults for display style of markdown but also gives all the means to tweak the appearance if desired. All markdown features are supported (including limited support for inlined HTML code, markdown tables and images).

<sup>*</sup>*This file is displayed by default in the [sample-apk] application. Which is a generic markdown viewer with support to display markdown via `http`, `https` & `file` schemes and 2 themes included: Light & Dark*

## Installation
```groovy
compile 'ru.noties:markwon:1.0.0'
compile 'ru.noties:markwon-image-loader:1.0.0' // optional
compile 'ru.noties:markwon-view:1.0.0' // optional
```

## Supported markdown features:
* Emphasis (`*`, `_`)
* Strong emphasis (`**`, `__`)
* Strike-through (`~~`)
* Headers (`#{1,6}`)
* Links (`[]()` && `[][]`)
* Images (_requires special handling_)
* Thematic break (`---`, `***`, `___`)
* Quotes & nested quotes (`>{1,}`)
* Ordered & non-ordered lists & nested ones
* Inline code
* Code blocks
* Tables (*with limitations*)
* Small subset of inline-html (which is rendered by this library):
* * Emphasis (`<i>`, `<em>`, `<cite>`, `<dfn>`)
* * Strong emphasis (`<b>`, `<strong>`)
* * SuperScript (`<sup>`)
* * SubScript (`<sub>`)
* * Underline (`<u>`)
* * Strike-through (`<s>`, `<strike>`, `<del>`)
  * other inline html is rendered via (`Html.fromHtml(...)`)

---

## Screenshots

Taken with default configuration (except for image loading):

<img src="./art/mw_light_01.png" width="33%" /> <img src="./art/mw_light_02.png" width="33%" />
<img src="./art/mw_light_03.png" width="33%" /> <img src="./art/mw_dark_01.png" width="33%" />

By default configuration uses TextView textColor for styling, so changing textColor changes style

---

## Quick start
This is the most simple way to set markdown to a TextView or any of its siblings:
```java
Markwon.setMarkdown(textView, markdown);
```

It's just a helper method, that does underneath:
* constructs a `Parser` (see: [commonmark-java][commonmark-java]) and parses markdown
* constructs a `SpannableConfiguration`
* *renders* parsed markdown to Spannable (via `SpannableRenderer`)
* prepares TextView to display images, tables and links
* sets text

This flow answers the most simple usage of displaying markdown: one shot parsing & configuration of relatively small markdown chunks. If your markdown contains a lot of text or you plan to display multiple UI widgets with markdown you might consider *stepping in* and taking control of this flow.

The candidate requirements to *step in*:
* parsing and processing of parsed markdown in background thread
* reusing `Parser` and/or `SpannableConfiguration` between multiple calls
* ignore images and tables specific logic (you know that markdown won't contain them)

So, if we expand `Markwon.setMarkdown(textView, markdown)` method we will see the following:
```java
// create a Parser instance (can be done manually)
// internally creates default Parser instance & registers `strike-through` & `tables` extension
final Parser parser = Markwon.createParser();

// core class to display markdown, can be obtained via this method,
// which creates default instance (no images handling though),
// or via `builder` method, which lets you to configure this instance
//
// `this` refers to a Context instance
final SpannableConfiguration configuration = SpannableConfiguration.create(this);

// it's better **not** to re-use this class between multiple calls
final SpannableRenderer renderer = new SpannableRenderer();

final Node node = parser.parse(markdown);
final CharSequence text = renderer.render(configuration, node);

// for links in markdown to be clickable
textView.setMovementMethod(LinkMovementMethod.getInstance());

// we need these due to the limited nature of Spannables to invalidate TextView
Markwon.unscheduleDrawables(textView);
Markwon.unscheduleTableRows(textView);

textView.setText(text);

Markwon.scheduleDrawables(textView);
Markwon.scheduleTableRows(textView);
```

Please refer to [SpannableConfiguration] document for more info

---

# Demo
Based on [this cheatsheet][cheatsheet]

---

## Headers
---
# Header 1
## Header 2
### Header 3
#### Header 4
##### Header 5
###### Header 6
---

## Emphasis

Emphasis, aka italics, with *asterisks* or _underscores_.

Strong emphasis, aka bold, with **asterisks** or __underscores__.

Combined emphasis with **asterisks and _underscores_**.

Strikethrough uses two tildes. ~~Scratch this.~~

---

## Lists
1. First ordered list item
2. Another item
  * Unordered sub-list.
1. Actual numbers don't matter, just that it's a number
  1. Ordered sub-list
4. And another item.

   You can have properly indented paragraphs within list items. Notice the blank line above, and the leading spaces (at least one, but we'll use three here to also align the raw Markdown).

   To have a line break without a paragraph, you will need to use two trailing spaces.
   Note that this line is separate, but within the same paragraph.
   (This is contrary to the typical GFM line break behaviour, where trailing spaces are not required.)

* Unordered list can use asterisks
- Or minuses
+ Or pluses

---

## Links

[I'm an inline-style link](https://www.google.com)

[I'm a reference-style link][Arbitrary case-insensitive reference text]

[I'm a relative reference to a repository file](../blob/master/LICENSE)

[You can use numbers for reference-style link definitions][1]

Or leave it empty and use the [link text itself].

---

## Code

Inline `code` has `back-ticks around` it.

<sup>*</sup>*Please note, that syntax highlighting is supported but library provides no means to do it automatically*

```javascript
var s = "JavaScript syntax highlighting";
alert(s);
```

```python
s = "Python syntax highlighting"
print s
```

```
No language indicated, so no syntax highlighting.
But let's throw in a <b>tag</b>.
```

---

## Tables

Colons can be used to align columns.

| Tables        | Are           | Cool  |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
| col 2 is      | centered      |   $12 |
| zebra stripes | are neat      |    $1 |

There must be at least 3 dashes separating each header cell.
The outer pipes (|) are optional, and you don't need to make the
raw Markdown line up prettily. You can also use inline Markdown.

Markdown | Less | Pretty
--- | --- | ---
*Still* | `renders` | **nicely**
1 | 2 | 3

---

## Blockquotes

> Blockquotes are very handy in email to emulate reply text.
> This line is part of the same quote.

Quote break.

> This is a very long line that will still be quoted properly when it wraps. Oh boy let's keep writing to make sure this is long enough to actually wrap for everyone. Oh, you can *put* **Markdown** into a blockquote.

Nested quotes
> Hello!
>> And to you!

---

## Inline HTML

<sup>*</sup>*As Android doesn't support HTML out of box, **Markwon** library supports only a small subset of it. Everything else is rendered via `Html.fromHtml()`*

* Emphasis (`<i>`, `<em>`, `<cite>`, `<dfn>`)
* Strong emphasis (`<b>`, `<strong>`)
* SuperScript (`<sup>`)
* SubScript (`<sub>`)
* Underline (`<u>`)
* Strike-through (`<s>`, `<strike>`, `<del>`)

Let's use it:
<u><i>H<sup>T<sub>M</sub></sup><b><s>L</s></b></i></u>

---

## Horizontal Rule

Three or more...

---

Hyphens (`-`)

***

Asterisks (`*`)

___

Underscores (`_`)

---


## License

```
  Copyright 2017 Dimitry Ivanov (mail@dimitryivanov.ru)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```

[sample-apk]: https://github.com/noties/Markwon/releases/download/v1.0.0/markwon-sample-1.0.0.apk
[commonmark-java]: https://github.com/atlassian/commonmark-java/blob/master/README.md
[cheatsheet]: https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet
[SpannableConfiguration]: ./docs/SpannableConfiguration.md

[arbitrary case-insensitive reference text]: https://www.mozilla.org
[1]: http://slashdot.org
[link text itself]: http://www.reddit.com
