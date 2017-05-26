# Markwon

[![maven|markwon](https://img.shields.io/maven-central/v/ru.noties/markwon.svg?label=maven%7Cmarkwon)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%markwon%22)
[![maven|markwon-image-loader](https://img.shields.io/maven-central/v/ru.noties/markwon.svg?label=maven%7Cmarkwon-image-loader)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%markwon%22)

Android library for rendering markdown as system-native Spannables. Based on [commonmark-java][commonmark-java]

## Installation
```groovy
compile 'ru.noties:markwon:1.0.0'
compile 'ru.noties:markwon-image-loader:1.0.0' // optional
```

---

**Please note, that this file is created for demonstration purposes. Please refer to `library` module [README][library] instead**

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
* Tables (*with limitations*)
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

`Lorem ipsum dolor sit amet`


### Code block
```
Lorem ipsum dolor sit amet
Lorem ipsum dolor sit amet
Lorem ipsum dolor sit amet
```


### H.T.M.L.
<b>O</b><i>K<s>A</s><sup>42<sup>43<sub><b>42</b></sub></sup></sup><u>Y</u></i>


### Tables
Header #1 | Header #2 | Header #3
---: | :---: | :---
content | content | content
long long long skjfs fgjsdfhj sf `dfk df` | sdsd,fklsdfklsdfklsdfkl sdfkl dsfjksdf sjkf jksdfjksdf sjkdf sdfkjsdjkf sdkjfs fkjsf sdkjfs fkjsd fkjsdf skjdf sdkjf skjfs fkjs fkjsdf jskdf sdjkf sjdkf sdkjf skjf sdkjf sdkjf sdfkjsd fkjsd fkjsdf sdkjfsjk dfkjsdf sdkjfs | yeah


|head #1| head #2|
|---|---|
| content | content |
| content | content |
| content | content |
| content | content |


[1]: https://github.com
[github]: https://github.com

[commonmark-java]: https://github.com/atlassian/commonmark-java/blob/master/README.md
[library]: https://github.com/noties/Markwon/blob/master/README.md