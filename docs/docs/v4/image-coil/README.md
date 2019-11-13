# Image Coil

<MavenBadge4 :artifact="'image-coil'" />

Image loading based on `Coil` library

```kt
val markwon = Markwon.builder(context)
        // automatically create Coil instance
        .usePlugin(CoilImagesPlugin.create(context))
        // if you need more control
        .usePlugin(CoilImagesPlugin.create(object : CoilImagesPlugin.CoilStore {
            override fun load(drawable: AsyncDrawable): LoadRequest {
                return LoadRequest(context, Coil.loader().defaults) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
            }

            override cancel(disposable: RequestDisposable) {
                disposable.dispose()
            }
        }))
        .build()
```