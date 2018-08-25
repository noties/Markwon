# Images

By default `Markwon` doesn't handle images. Although `AsyncDrawable.Loader` is
defined in main artifact, it does not provide implementation.

The interface is pretty simple:

```java
public interface Loader {

    void load(@NonNull String destination, @NonNull AsyncDrawable drawable);

    void cancel(@NonNull String destination);
}
```

## AsyncDrawableLoader

<MavenBadge artifact="markwon-image-loader" />

`AsyncDrawableLoader` from `markwon-image-loader` artifact can be used.

:::tip Install
[Learn how to add](/docs/install.md#image-loader) `markwon-image-loader` to your project
:::

Default instance of `AsyncDrawableLoader` can be obtain like this:

```java
AsyncDrawableLoader.create();
```

### Scheme support

By default `AsyncDrawableLoader` handles these URL schemes:
* `file` (including reference to `android_assets`)
* `data` <Badge text="2.0.0" /> ([wiki](https://en.wikipedia.org/wiki/Data_URI_scheme))
  for inline image references
* all other schemes are considered to be network related and will be tried to obtain
  from network

#### Data <Badge text="2.0.0" />

`data` scheme handler supports both `base64` encoded content and `plain`:

```html
<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==" alt="Red dot" />
```

```html
<img src='data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" aria-hidden="true" x="0px" y="0px" viewBox="0 0 100 100" width="15" height="15" class="icon outbound"><path fill="currentColor" d="M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z"></path> <polygon fill="currentColor" points="45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9"></polygon></svg>' >
```

:::warning Note
Data uri works with native markdown images, but only in base64 mode:
```markdown
![svg](data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGFyaWEtaGlkZGVuPSJ0cnVlIiB4PSIwcHgiIHk9IjBweCIgdmlld0JveD0iMCAwIDEwMCAxMDAiIHdpZHRoPSIxNSIgaGVpZ2h0PSIxNSIgY2xhc3M9Imljb24gb3V0Ym91bmQiPjxwYXRoIGZpbGw9ImN1cnJlbnRDb2xvciIgZD0iTTE4LjgsODUuMWg1NmwwLDBjMi4yLDAsNC0xLjgsNC00di0zMmgtOHYyOGgtNDh2LTQ4aDI4di04aC0zMmwwLDBjLTIuMiwwLTQsMS44LTQsNHY1NkMxNC44LDgzLjMsMTYuNiw4NS4xLDE4LjgsODUuMXoiPjwvcGF0aD4gPHBvbHlnb24gZmlsbD0iY3VycmVudENvbG9yIiBwb2ludHM9IjQ1LjcsNDguNyA1MS4zLDU0LjMgNzcuMiwyOC41IDc3LjIsMzcuMiA4NS4yLDM3LjIgODUuMiwxNC45IDYyLjgsMTQuOSA2Mi44LDIyLjkgNzEuNSwyMi45Ij48L3BvbHlnb24+PC9zdmc+)
```
:::

## Configuration

If you wish to configure `AsyncDrawableLoader` `#builder` factory method can be used:

```java
AsyncDrawableLoader.builder()
        .build();
```

### OkHttp client

```java
AsyncDrawableLoader.builder()
        .client(OkHttpClient)
        .build();
```

If not provided explicitly, default `new OkHttpClient()` will be used

### Resources

`android.content.res.Resources` to be used when obtaining an image
from Android assets folder **and** to create Bitmaps.

```java
AsyncDrawableLoader.builder()
        .resources(Resources)
        .build();
```

If not provided explicitly, default `Resources.getSystem()` will be used.

:::warning
`Resources.getSystem()` can have unexpected side-effects (plus loading from 
assets won't work). As a rule of thumb
always provide `AsyncDrawableLoader` with your Application's `Resources`. 
To quote Android documentation for `#getSystem` method:

> Return a global shared Resources object that provides access to only 
  system resources (no application resources), and is not configured 
  for the current screen (can not use dimension  units, does not 
  change based on orientation, etc).

:::

### Executor service

`ExecutorService` to be used to download images in background thread

```java
AsyncDrawableLoader.builder()
        .executorService(ExecutorService)
        .build();
```

If not provided explicitly, default `okHttpClient.dispatcher().executorService()` will be used

### Error drawable

`errorDrawable` to be used when image loader encountered an error loading image

```java
AsyncDrawableLoader.builder()
        .errorDrawable(Drawable)
        .build();
```

if not provided explicitly, default `null` value will be used.

### Media decoder <Badge text="1.1.0" />

`MediaDecoder` is a simple asbtraction that encapsulates handling
of a specific image type.

```java
AsyncDrawableLoader.builder()
        .mediaDecoders(MediaDecoder...)
        .mediaDecoders(List<MediaDecoder>)
        .build();
```

If not provided explicitly, default `MediaDecoder`s will be used (SVG, GIF, plain) with 
provided `Resources` and `gif-autoplay=true`

`markwon-image-loader` comes with 3 `MediaDecoder` implementations:
* `SvgMediaDecoder` (based on [androidsvg](https://github.com/BigBadaboom/androidsvg))
* `GifMediaDecoder` (based on [android-gif-drawable](https://github.com/koral--/android-gif-drawable))
* `ImageMediaDecoder` (handling all _plain_ images)

:::tip
Always add a _generic_ `MediaDecoder` instance at the end of the list. 
Order does matter. For example:
```java{5}
AsyncDrawableLoader.builder()
        .mediaDecoders(
                SvgMediaDecoder.create(Resources),
                GifMediaDecoder.create(boolean),
                ImageMediaDecoder.create(Resources)
        )
.build();
```
:::

#### SvgMediaDecoder

```java
SvgMediaDecoder.create(Resources)
```

#### GifMediaDecoder

```java
GifMediaDecoder.create(boolean)
```

`boolean` argument stands for `autoPlayGif`

#### ImageMediaDecoder

```java
ImageMediaDecoder.create(Resources)
```