# Image Coil

<MavenBadge4 :artifact="'image-coil'" />

Image loading based on `Coil` library

```kt
val markwon = Markwon.builder(context)
        // automatically create Coil instance
        .usePlugin(CoilImagesPlugin.create(context))
        // if you need more control
        .usePlugin(CoilImagesPlugin.create(object : CoilImagesPlugin.CoilStore() {
            override load(drawable: AsyncDrawable, target: Target): RequestDisposable {
                return Coil.load(context, drawable.destination) {
                    target(target)
                }
            }

            override cancel(requestDisposable: RequestDisposable) {
                requestDisposable.dispose()
            }
        }))
        .build()
```