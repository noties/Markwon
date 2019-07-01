# Image Picasso

<MavenBadge4 :artifact="'image-picasso'" />

Image loading based on `Picasso` library

```java
final Markwon markwon = Markwon.builder(context)
        // automatically create Picasso instance
        .usePlugin(PicassoImagesPlugin.create(context))
        // use provided picasso instance
        .usePlugin(PicassoImagesPlugin.create(Picasso.get()))
        // if you need more control
        .usePlugin(PicassoImagesPlugin.create(new PicassoImagesPlugin.PicassoStore() {
            @NonNull
            @Override
            public RequestCreator load(@NonNull AsyncDrawable drawable) {
                return Picasso.get()
                        .load(drawable.getDestination())
                        // please note that drawable should be used as tag (not a destination)
                        // otherwise there won't be support for multiple images with the same URL 
                        .tag(drawable);
            }

            @Override
            public void cancel(@NonNull AsyncDrawable drawable) {
                Picasso.get()
                        .cancelTag(drawable);
            }
        }))
        .build();
```