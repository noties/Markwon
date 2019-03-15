# Configuration

`SpannableConfiguration` is the core component that controls how markdown is parsed and rendered.
It can be obtained via factory methods:

```java
// creates default implementation
final SpannableConfiguration configuration = SpannableConfiguration.create(context);
```

```java
// creates configurablable instance via `#builder` method
final SpannableConfiguration configuration = SpannableConfiguration.builder(context)
        .asyncDrawableLoader(AsyncDrawableLoader.create())
        .build();
```

:::tip Note
If `#builder` factory method is used, you do not need to specify default
values as they will be applied automatically
:::

:::warning Images
If you plan on using images inside your markdown/HTML, you will have to **explicitly**
register an implementation of `AsyncDrawable.Loader` via `#asyncDrawableLoader` builder method.
`Markwon` comes with ready implementation for that and it can be found in
`markwon-image-loader` module. Refer to module [documentation](/docs/v2/image-loader.md)
:::

## Theme

`SpannableTheme` controls how markdown is rendered. It has pretty extensive number of
options that can be found [here](/docs/v2/theme.md)

```java
SpannableConfiguration.builder(context)
        .theme(SpannableTheme)
        .build();
```

If `SpannableTheme` is not provided explicitly, `SpannableTheme.create(context)` will be used

## Images

### Async loader

`AsyncDrawable.Loader` handles images in your markdown and HTML

```java
SpannableConfiguration.builder(context)
        .asyncDrawableLoader(AsyncDrawable.Loader)
        .build();
```

If `AsyncDrawable.Loader` is not provided explicitly, default **no-op** implementation will be used.

:::tip Implementation
There are no restrictions on what implementation to use, but `Markwon` has artifact that can
answer the most common needs of displaying SVG, GIF and other image formats. It can be found [here](/docs/v2/image-loader.md)
:::

### Size resolver <Badge text="1.0.1" />

`ImageSizeResolver` controls the size of an image to be displayed. Currently it
handles only HTML images (specified via `img` tag).

```java
SpannableConfiguration.builder(context)
        .imageSizeResolver(ImageSizeResolver)
        .build();
```

If not provided explicitly, default `ImageSizeResolverDef` implementation will be used.
It handles 3 dimention units:
* `%` (percent)
* `em` (relative to text size)
* `px` (absolute size, every dimention that is not `%` or `em` is considered to be _absolute_)

```html
<img width="100%">
<img width="2em" height="10px">
<img style="{width: 100%; height: 8em;}">
```

`ImageSizeResolverDef` keeps the ratio of original image if one of the dimentions is missing.

:::warning Height%
There is no support for `%` units for `height` dimention. This is due to the fact that
height of an TextView in which markdown is displayed is non-stable and changes with time
(for example when image is loaded and applied to a TextView it will _increase_ TextView's height),
so we will have no point-of-refence from which to _calculate_ image height.
:::

## Syntax highlight

`SyntaxHighlight` controls the syntax highlight for code blocks (in markdown).

```java
SpannableConfiguration.builder(context)
        .syntaxHighlight(SyntaxHighlight)
        .build();
```

If not provided explicitly, default **no-op** implementation will be used.

:::tip Syntax highlight
Although `SyntaxHighlight` interface was included with the very first version
of `Markwon` there were no ready-to-use implementations. But starting with <Badge text="1.1.0" /> 
`Markwon` provides one. It can be found in `markwon-syntax-highlight` artifact. Refer
to module [documentation](/docs/v2/syntax-highlight.md)
:::

## Link resolver

`LinkSpan.Resolver` is triggered when a link is clicked in markdown/HTML.

```java
SpannableConfiguration.builder(context)
        .linkResolver(LinkSpan.Resolver)
        .build();
```

If not provided explicitly, default `LinkResolverDef` implementation will be used.
Underneath it constructs an `Intent` and _tries_ to start an Activity associated with it.
It no Activity is found, it will silently fail (no runtime exceptions)

## URL processor

`UrlProcessor` is used to process found URLs in markdown/HTML.

```java
SpannableConfiguration.builder(context)
        .urlProcessor(UrlProcessor)
        .build();
```

If not provided explicitly, default **no-op** implementation will be used.

`Markwon` provides 2 implementations of `UrlProcessor`:
* `UrlProcessorRelativeToAbsolute`
* `UrlProcessorAndroidAssets`

### UrlProcessorRelativeToAbsolute

`UrlProcessorRelativeToAbsolute` can be used to make relative URL absolute. For example if an image is
defined like this: `![img](./art/image.JPG)` and `UrlProcessorRelativeToAbsolute`
is created with `https://github.com/noties/Markwon/raw/master/` as the base: 
`new UrlProcessorRelativeToAbsolute("https://github.com/noties/Markwon/raw/master/")`,
then final image will have `https://github.com/noties/Markwon/raw/master/art/image.JPG`
as the destination.

### UrlProcessorAndroidAssets

`UrlProcessorAndroidAssets` can be used to make processed links to point to Android assets folder.
So an image: `![img](./art/image.JPG)` will have `file:///android_asset/art/image.JPG` as the
destination

## Factory <Badge text="1.1.0" />

`SpannableFactory` is used to control _what_ span implementations to be used

```java
SpannableConfiguration.builder(context)
        .factory(SpannableFactory)
        .build();
```

If not provided explicitly, default `SpannableFactoryDef` implementation will be used. It is documented
in [this section](/docs/v2/factory.md)

## Soft line break <Badge text="1.1.1" />

`softBreakAddsNewLine` option controls how _soft breaks_ are treated in the final result.
If `true` -> soft break will add a new line, else it will add a ` ` (space) char.

```java
SpannableConfiguration.builder(context)
        .softBreakAddsNewLine(boolean)
        .build();
```

If not provided explicitly, default `false` value will be used.

<Link name="commonmark-spec#soft-break" displayName="Commonmark specification" />

## HTML <Badge text="2.0.0" />

### Parser

`MarkwonHtmlParser` is used to parse HTML content

```java
SpannableConfiguration.builder(context)
        .htmlParser(MarkwonHtmlParser)
        .build();
```

if not provided explicitly, default `MarkwonHtmlParserImpl` will be used
**if** it can be found in classpath, otherwise default **no-op** implementation
wiil be used. Refer to [HTML](/docs/v2/html.md#parser) document for more information about this behavior.

### Renderer

`MarkwonHtmlRenderer` controls how parsed HTML content will be rendered.

```java
SpannableConfiguration.builder(context)
        .htmlRenderer(MarkwonHtmlRenderer)
        .build();
```

If not provided explicitly, default `MarkwonHtmlRenderer` implementation will be used.
It is documented [here](/docs/v2/html.md#renderer)

### HTML allow non-closed tags

`htmlAllowNonClosedTags` option is used to control whether or not to
render non-closed HTML tags

```java
SpannableConfiguration.builder(context)
        .htmlAllowNonClosedTags(boolean)
        .build();
```

If not provided explicitly, default value `false` will be used (non-closed tags **won't** be rendered).
