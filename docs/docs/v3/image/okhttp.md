# Image OkHttp

<LegacyWarning />

<MavenBadge :artifact="'image-okhttp'" />

Uses [okhttp library](https://github.com/square/okhttp) as the network transport fro images. Since <Badge text="3.0.0" />
`Markwon` uses a system-native `HttpUrlConnection` and does not rely on any
3rd-party tool to download resources from network. It can answer the most common needs,
but if you would like to have a custom redirect policy or add an explicit caching
of downloaded resources OkHttp might be a better option.

```java
final Markwon markwon = Markwon.builder(context)
        // it's required to register ImagesPlugin
        .usePlugin(ImagesPlugin.create(context))
        
        // will create default instance of OkHttpClient
        .usePlugin(OkHttpImagesPlugin.create())
        
        // or accept a configured client
        .usePlugin(OkHttpImagesPlugin.create(new OkHttpClient()))
        .build();
```

## Proguard
```proguard
-dontwarn okhttp3.**
-dontwarn okio.**
```