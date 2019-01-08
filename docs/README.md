---
title: 'Introduction'
---

<img :src="$withBase('./art/markwon_logo.png')" alt="Markwon Logo" width="50%">

<br><br>
[![markwon](https://img.shields.io/maven-central/v/ru.noties.markwon/core.svg?label=markwon)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties.markwon%22%20)
[![Build Status](https://travis-ci.org/noties/Markwon.svg?branch=master)](https://travis-ci.org/noties/Markwon)

**Markwon** is a markdown library for Android. It parses markdown following 
<Link name="commonmark-spec" /> with the help of amazing <Link name="commonmark-java" /> library
and renders result as _Android-native_ Spannables. **No HTML** is involved
as an intermediate step. <u>**No WebView** is required</u>. It's extremely fast, 
feature-rich and extensible.

It gives ability to display markdown in all TextView widgets (**TextView**, 
**Button**, **Switch**, **CheckBox**, etc), **Toasts** and all other places that accept
**Spanned content**. Library provides reasonable defaults to display style of a markdown content
but also gives all the means to tweak the appearance if desired. All markdown features 
listed in <Link name="commonmark-spec" /> are supported (including support for **inlined/block HTML code**, 
**markdown tables**, **images** and **syntax highlight**).

## Supported markdown features

* Emphasis (`*`, `_`)
* Strong emphasis (`**`, `__`)
* Strike-through (`~~`)
* Headers (`#{1,6}`)
* Links (`[]()` && `[][]`)
* [Images](/docs/image-loader.md)
* Thematic break (`---`, `***`, `___`)
* Quotes & nested quotes (`>{1,}`)
* Ordered & non-ordered lists & nested ones
* Inline code
* Code blocks
* Tables (*with limitations*)
* [Syntax highlight](/docs/syntax-highlight.md)
* [HTML](/docs/html.md)
  * Emphasis (`<i>`, `<em>`, `<cite>`, `<dfn>`)
  * Strong emphasis (`<b>`, `<strong>`)
  * SuperScript (`<sup>`)
  * SubScript (`<sub>`)
  * Underline (`<u>`, `ins`)
  * Strike-through (`<s>`, `<strike>`, `<del>`)
  * Link (`a`)
  * Lists (`ul`, `ol`)
  * Images (`img` will require configured image loader)
  * Blockquote (`blockquote`)
  * Heading (`h1`, `h2`, `h3`, `h4`, `h5`, `h6`)
  * there is support to render any HTML tag, but it will require to create a special `TagHandler`,
    more information can be found in [HTML section](/docs/html.md#custom-tag-handler)
* Task lists:
- [ ] Not _done_
  - [X] **Done** with `X`
  - [x] ~~and~~ **or** small `x`

## Screenshots

<img :src="$withBase('/art/mw_light_01.png')" alt="screenshot light #1" width="30%">
<img :src="$withBase('/art/mw_light_02.png')" alt="screenshot light #2" width="30%">
<img :src="$withBase('/art/mw_light_03.png')" alt="screenshot light #3" width="30%">
<img :src="$withBase('/art/mw_dark_01.png')" alt="screenshot dark #2" width="30%">

By default configuration uses TextView textColor for styling, so changing textColor changes style

:::tip Sample application
Screenshots are taken from sample application. It is a generic markdown viewer 
with support to display markdown content via `http`, `https` &amp; `file` schemes 
and 2 themes included: Light &amp; Dark. It can be downloaded from [releases](https://github.com/noties/Markwon/releases)
:::
