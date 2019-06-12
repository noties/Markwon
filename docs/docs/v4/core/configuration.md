# Configuration

`MarkwonConfiguration` class holds common Markwon functionality.
These are _configurable_ properties:
* `AsyncDrawableLoader` (back here since <Badge text="4.0.0" />)
* `SyntaxHighlight`
* `LinkSpan.Resolver`
* `UrlProcessor`
* `ImageSizeResolver`

:::tip
Additionally `MarkwonConfiguration` holds:
* `MarkwonTheme`
* `MarkwonSpansFactory`

Please note that these values can be retrieved from `MarkwonConfiguration`
instance, but their _configuration_ must be done by a `Plugin` by overriding
one of the methods:
* `Plugin#configureTheme`
* `Plugin#configureSpansFactory`
:::

## AsyncDrawableLoader

Allows loading and displaying of images in markdown. Please note that if one is not specified
directly (or via plugin) no images will be displayed.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                builder.asyncDrawableLoader(AsyncDrawableLoader.noOp());
            }
        })
        .build();
```

Currently `Markwon` provides 3 implementations for loading images:
* [own implementation](/docs/v4/image.md) with SVG, GIF, data uri and android_assets support
* [based on Picasso](/docs/v4/image-picasso.md)
* [based on Glide](/docs/v4/image-glide.md)

## SyntaxHighlight

```java
final Markwon markwon = Markwon.builder(this)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                builder.syntaxHighlight(new SyntaxHighlightNoOp());
            }
        })
        .build();
```

:::tip
Use [syntax-highlight](/docs/v4/syntax-highlight/) to add syntax highlighting
to your application
:::

## LinkSpan.Resolver

React to a link click event. By default `LinkResolverDef` is used,
which tries to start an Activity given the `link` argument. If no
Activity can handle `link` `LinkResolverDef` silently ignores click event

```java
final Markwon markwon = Markwon.builder(this)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                builder.linkResolver(new LinkSpan.Resolver() {
                    @Override
                    public void resolve(View view, @NonNull String link) {
                        // react to link click here
                    }
                });
            }
        })
        .build();
```

:::tip
Please note that `Markwon` will apply `LinkMovementMethod` to a resulting TextView
if there is none registered. if you wish to register own instance of a `MovementMethod`
apply it directly to a TextView or use [MovementMethodPlugin](/docs/v4/core/movement-method-plugin.md)
:::

## UrlProcessor

Process URLs in your markdown (for links and images). If not provided explicitly, 
default **no-op** implementation will be used, which does not modify URLs (keeping them as-is).

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
destination.

:::tip
Please note that `UrlProcessorAndroidAssets` will process only URLs that have no `scheme` information,
so a `./art/image.png` will become `file:///android_asset/art/image.JPG` whilst `https://so.me/where.png`
will be kept as-is.
:::

## ImageSizeResolver

`ImageSizeResolver` controls the size of an image to be displayed. Currently it
handles only HTML images (specified via `img` tag).

```java
final Markwon markwon = Markwon.builder(this)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                builder.imageSizeResolver(new ImageSizeResolver() {
                    @NonNull
                    @Override
                    public Rect resolveImageSize(
                            @Nullable ImageSize imageSize,
                            @NonNull Rect imageBounds,
                            int canvasWidth,
                            float textSize) {
                        return null;
                    }
                });
            }
        })
        .build();
```

If not provided explicitly, default `ImageSizeResolverDef` implementation will be used.
It handles 3 dimension units:
* `%` (percent, relative to Canvas width)
* `em` (relative to text size)
* `px` (absolute size, every dimension that is not `%` or `em` is considered to be _absolute_)

```html
<img width="100%">
<img width="2em" height="10px">
<img style="{width: 100%; height: 8em;}">
```

`ImageSizeResolverDef` keeps the ratio of original image if one of the dimensions is missing.

:::warning Height%
There is no support for `%` units for `height` dimension. This is due to the fact that
height of an TextView in which markdown is displayed is non-stable and changes with time
(for example when image is loaded and applied to a TextView it will _increase_ TextView's height),
so we will have no point-of-reference from which to _calculate_ image height.
:::

:::tip
`ImageSizeResolverDef` also takes care for an image to **not** exceed
canvas width. If an image has greater width than a TextView Canvas, then
image will be _scaled-down_ to fit the canvas. Please note that this rule
applies only if image has no absolute sizes (for example width is specified
in pixels).
:::