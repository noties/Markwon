---
title: 'Overview'
---

<img :src="$withBase('./art/markwon_logo.png')" alt="Markwon Logo" width="50%">

<br><br>
<MavenBadges/>

**Markwon** is a markdown library for Android. It parses markdown 
following [commonmark spec] with the help
of amazing [commonmark-java](https://github.com/atlassian/commonmark-java/) library
and renders result as _Android-native_ Spannables. **No HTML** is involved
as an intermediate step. <u>**No WebView** is required</u>. It's extremely fast, 
feature-rich and extensible.

It gives ability to display markdown in all TextView widgets (**TextView**, 
**Button**, **Switch**, **CheckBox**, etc), **Toasts** and all other places that accept
**Spanned content**. Library provides reasonable defaults to display style of a markdown content
but also gives all the means to tweak the appearance if desired. All markdown features 
listed in [commonmark spec] are supported (including support for **inlined/block HTML code**, 
**markdown tables**, **images** and **syntax highlight**).

[commonmark spec]: https://spec.commonmark.org/