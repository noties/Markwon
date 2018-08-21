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