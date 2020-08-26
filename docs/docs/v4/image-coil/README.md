# Image Coil

<MavenBadge4 :artifact="'image-coil'" />

Image loading based on [Coil](https://github.com/coil-kt/coil/) library.

There are 3 factory methods to obtain `CoilImagesPlugin`:

```kotlin
val coilPlugin: CoilImagesPlugin
  get() = CoilImagesPlugin.create(context)
```

:::warning
In order to use the `CoilImagesPlugin.create(Context)` factory method your
app must have **explicit** dependency on `coil` (`io.coil-kt:coil`) library. This artifact
relies on `io.coil-kt:coil-base` as per [Coil documentation](https://coil-kt.github.io/coil/getting_started/#artifacts)
:::

```kotlin
val coilPlugin: CoilImagesPlugin
  get() = CoilImagesPlugin.create(context, imageLoader)

val imageLoader: ImageLoader
  get() = ImageLoader.Builder(context)
    .apply {
      availableMemoryPercentage(0.5)
      bitmapPoolPercentage(0.5)
      crossfade(true)
    }
    .build()
```

```kotlin
val coilPlugin: CoilImagesPlugin
  get() {
    val loader = imageLoader
    return CoilImagesPlugin.create(
      object : CoilImagesPlugin.CoilStore {
        override fun load(drawable: AsyncDrawable): ImageRequest {
          return ImageRequest.Builder(context)
            .defaults(loader.defaults)
            .data(drawable.destination)
            .crossfade(true)
            .transformations(CircleCropTransformation())
            .build()
        }

        override fun cancel(disposable: Disposable) {
          disposable.dispose()
        }
      },
      loader
    )
  }

val imageLoader: ImageLoader
  get() = ImageLoader.Builder(context)
    .apply {
      availableMemoryPercentage(0.5)
      bitmapPoolPercentage(0.5)
      crossfade(true)
    }
    .build()
```

Finally, use as a regular plugin:

```kotlin
val markwon = Markwon.builder(context)
  .usePlugin(coilPlugin)
  .build()

```
