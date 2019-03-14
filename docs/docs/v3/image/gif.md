# Image GIF

<MavenBadge :artifact="'image-gif'" />

Adds support for GIF images inside markdown. 
Relies on [android-gif-drawable library](https://github.com/koral--/android-gif-drawable)

```java
final Markwon markwon = Markwon.builder(context)
        // it's required to register ImagesPlugin
        .usePlugin(ImagesPlugin.create(context))
        // add GIF support for images
        .usePlugin(GifPlugin.create())
        .build();
```