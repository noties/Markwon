# Image Coil

<MavenBadge4 :artifact="'image-coil'" />

Image loading based on `Coil` library.

```kotlin
val markwon = Markwon.builder(context)
        // automatically create Coil instance
        .usePlugin(CoilImagesPlugin.create(context))
        // use supplied ImageLoader instance
        .usePlugin(CoilImagesPlugin.create(
            context,
            ImageLoader(context) {
                availableMemoryPercentage(0.5)
                bitmapPoolPercentage(0.5)
                crossfade(true)
            }
        ))
        // if you need more control
        .usePlugin(CoilImagesPlugin.create(object : CoilImagesPlugin.CoilStore {
            override fun load(drawable: AsyncDrawable): LoadRequest {
                return LoadRequest(context, customImageLoader.defaults) {
                    data(drawable.destination)
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
            }

            override cancel(disposable: RequestDisposable) {
                disposable.dispose()
            }
        }, customImageLoader))
        .build()
```

:::warning
In order to use the `CoilImagesPlugin.create(Context)` factory method your
app must have **explicit** dependency on `coil` library
:::