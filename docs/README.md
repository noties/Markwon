---
title: 'Introduction'
---

<img :src="$withBase('/art/markwon_logo.png')" alt="Markwon Logo" width="50%">

<br><br>
[![markwon](https://img.shields.io/maven-central/v/io.noties.markwon/core.svg?label=markwon)](http://search.maven.org/#search|ga|1|g%3A%22io.noties.markwon%22%20)
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

Since version <Badge text="4.2.0" /> **Markwon** comes with an [editor] to _highlight_ markdown input
as user types for example in **EditText**.

[editor]: /docs/v4/editor/

## Supported markdown features

* Emphasis (`*`, `_`)
* Strong emphasis (`**`, `__`)
* Headers (`#{1,6}`)
* Links (`[]()` && `[][]`)
* [Images](/docs/v4/image/)
* Thematic break (`---`, `***`, `___`)
* Quotes & nested quotes (`>{1,}`)
* Ordered & non-ordered lists & nested ones
* Inline code
* Code blocks
* [Strike-through](/docs/v4/ext-strikethrough/) (`~~`)
* [Tables](/docs/v4/ext-tables/) (*with limitations*)
* [Syntax highlight](/docs/v4/syntax-highlight/)
* [LaTeX](/docs/v4/ext-latex/) formulas
* [HTML](/docs/v4/html/)
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
  * there is support to render any HTML/XML tag, but it will require to create a special `TagHandler`,
    more information can be found in [HTML section](/docs/v4/html/#taghandler)
* [Task lists](/docs/v4/ext-tasklist/):
<ul style="list-style-type: none; margin: 0; padding: 0;">
<li><input type="checkbox" disabled>Not <i>done</i></li>
<li><input type="checkbox" disabled checked><strong>Done</strong> with <code>X</code></li>
<li><input type="checkbox" disabled checked><del>and</del> <strong>or</strong> small <code>x</code></li>    
</ul>

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


## Awesome Markwon

<u>Applications using Markwon</u>:

* [Partico](https://partiko.app/) - Partiko is a censorship free social network.
* [FairNote](https://play.google.com/store/apps/details?id=com.rgiskard.fairnote) - simple and intuitive notepad app. It gives you speed and efficiency when you write notes, to-do lists, e-mails, or jot down quick ideas.
* [Boxcryptor](https://www.boxcryptor.com) - A software that adds AES-256 and RSA encryption to Dropbox, Google Drive, OneDrive and many other clouds.

<AwesomeGroup :apps="[
    {name: 'Cinopsys: Movies and Shows', image: 'http://drive.google.com/uc?export=view&id=1rD0HLd8tDUCe8QcVEG_iGvsJbFyozRhC', link: 'https://play.google.com/store/apps/details?id=com.cinopsys.movieshows'}
]" />

<u>Extension/plugins</u>:

* [MarkwonCodeEx](https://github.com/kingideayou/MarkwonCodeEx) - Markwon extension support elegant code background.

---

[Help to improve][awesome_link] this section by submitting your application or library
that is using `Markwon`


[awesome_link]: https://github.com/noties/Markwon/issues/new?labels=awesome&body=Please%20provide%20the%20following%3A%0A*%20Project%20name%0A*%20Project%20URL%20(repository%2C%20store%20listing%2C%20web%20page)%0A*%20Optionally%20_brand_%20image%20URL%0A%0APlease%20make%20sure%20that%20there%20is%20the%20**awesome**%20label%20selected%20for%20this%20issue.%0A%0A%F0%9F%99%8C%20
