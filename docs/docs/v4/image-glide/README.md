# Image Glide

<MavenBadge4 :artifact="'image-glide'" />

Image loading based on `Glide` library

```java
final Markwon markwon = Markwon.builder(context)
        // automatically create Glide instance
        .usePlugin(GlideImagesPlugin.create(context))
        // use supplied Glide instance
        .usePlugin(GlideImagesPlugin.create(Glide.with(context)))
        // if you need more control
        .usePlugin(GlideImagesPlugin.create(new GlideImagesPlugin.GlideStore() {
            @NonNull
            @Override
            public RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable) {
                return Glide.with(context).load(drawable.getDestination());
            }

            @Override
            public void cancel(@NonNull Target<?> target) {
                Glide.with(context).clear(target);
            }
        }))
        .build();
```