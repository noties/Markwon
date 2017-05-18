# Markwon
Android library for rendering markdown as system-native Spannables. Based on [commonmark-java][commonmark-java]

**WIP (*work in progress*)**

---

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
* Small subset of inline-html (which is rendered by this library):
* * Emphasis (`<i>`, `<em>`, `<cite>`, `<dfn>`)
* * Strong emphasis (`<b>`, `<strong>`)
* * SuperScript (`<sup>`)
* * SubScript (`<sub>`)
* * Underline (`<u>`)
* * Strike-through (`<s>`, `<strike>`, `<del>`)
  * other inline html is rendered via (`Html.fromHtml(...)`)

### Emphasis
*Lorem ipsum dolor sit amet*
_Lorem ipsum dolor sit amet_
<i>Lorem ipsum dolor sit amet</i>
<em>Lorem ipsum dolor sit amet</em>
<cite>Lorem ipsum dolor sit amet</cite>
<dfn>Lorem ipsum dolor sit amet</dfn>

### Strong emphasis
**Lorem ipsum dolor sit amet**
__Lorem ipsum dolor sit amet__
<b>Lorem ipsum dolor sit amet</b>
<strong>Lorem ipsum dolor sit amet</strong>

### Strike-through
~~Lorem ipsum dolor sit amet~~
<s>Lorem ipsum dolor sit amet</s>
<strike>Lorem ipsum dolor sit amet</strike>
<del>Lorem ipsum dolor sit amet</del>

---
# Header 1
## Header 2
### Header 3
#### Header 4
##### Header 5
###### Header 6
---

### Links
[click me](https://github.com)
[click me][1]
[click me][github]
<a href="https://github.com">click me</a>

### Images
// todo, normal ones & svg & gif

### Thematic break
---
***
___

### Quotes
> Lorem ipsum dolor sit amet
>> Lorem ipsum dolor sit amet
>>> Lorem ipsum dolor sit amet

### Ordered lists
1. Lorem ipsum dolor sit amet
2. Lorem ipsum dolor sit amet
   1. Lorem ipsum dolor sit amet
      1. Lorem ipsum dolor sit amet
   2. Lorem ipsum dolor sit amet
3. Lorem ipsum dolor sit amet

### Non-ordered lists
* Lorem ipsum dolor sit amet
   * Lorem ipsum dolor sit amet
   * * Lorem ipsum dolor sit amet
*  * * * Lorem ipsum dolor sit amet
* * Lorem ipsum dolor sit amet
* Lorem ipsum dolor sit amet

### Inline code
`Lorem` ipsum dolor sit amet
Lorem `ipsum` dolor sit amet
Lorem ipsum `dolor` sit amet
Lorem ipsum dolor `sit` amet
Lorem ipsum dolor sit `amet`

### Code block
// todo syntax higlight
```
Lorem ipsum dolor sit amet
Lorem ipsum dolor sit amet
Lorem ipsum dolor sit amet
```


[1]: https://github.com
[github]: https://github.com
[commonmark-java]: https://github.com/atlassian/commonmark-java