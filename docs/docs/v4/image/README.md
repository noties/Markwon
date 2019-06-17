# Image

<MavenBadge4 :artifact="'image'" />

In order to display images in your markdown `ImagesPlugin` can be used.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create())
```

:::tip
There are also modules that add image loading capabilities to markdown
based on image-loading libraries: [image-glide](/docs/v4/image-glide/) and
[image-picasso](/docs/v4/image-picasso/)
:::

`ImagesPlugin` splits the image-loading into 2 parts: scheme-handling and media-decoding.

## SchemeHandler

To add a scheme-handler to `ImagesPlugin`:

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create())
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configure(@NonNull Registry registry) {
                registry.require(ImagesPlugin.class, new Action<ImagesPlugin>() {
                    @Override
                    public void apply(@NonNull ImagesPlugin imagesPlugin) {
                        imagesPlugin.addSchemeHandler(DataUriSchemeHandler.create());
                    }
                });
            }
        })
```

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create(new ImagesPlugin.ImagesConfigure() {
            @Override
            public void configureImages(@NonNull ImagesPlugin plugin) {
                plugin.addSchemeHandler(DataUriSchemeHandler.create());
            }
        }))
```

`ImagesPlugin` comes with a set of predefined scheme-handlers:
* `FileSchemeHandler` - `file://`
* `DataUriSchemeHandler` - `data:`
* `NetworkSchemeHandler` - `http`, `https`
* `OkHttpNetworkSchemeHandler` - `http`, `https`

### FileSchemeHandler

Loads images via `file://` scheme. Allows loading images from `assets` folder.

```java
// default implementation, no assets handling
FileSchemeHandler.create();

// assets loading
FileSchemeHandler.createWithAssets(context);
```

:::warning
Assets loading will work when your URL will include `android_asset` the the path,
for example: `file:///android_asset/image.png` (mind the 3 slashes `///`). If you wish
to _assume_ all images without proper scheme to point to assets folder, then you can use
[UrlProcessorAndroidAssets](/docs/v4/core/configuration.html#urlprocessorandroidassets)
:::

By default `ImagesPlugin` includes _plain_ `FileSchemeHandler` (without assets support),
so if you wish to change that you can explicitly specify it:

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create(new ImagesPlugin.ImagesConfigure() {
            @Override
            public void configureImages(@NonNull ImagesPlugin plugin) {
                plugin.addSchemeHandler(FileSchemeHandler.createWithAssets(context));
            }
        }))
```

### DataUriSchemeHandler
`DataUriSchemeHandler` allows _inlining_ images with `data:` scheme (`data:image/svg+xml;base64,MTIz`).
This scheme-handler is registered by default, so you do not need to add it explicitly.

### NetworkSchemeHandler
`NetworkSchemeHandler` allows obtaining images from `http://` and `https://` uris 
(internally it uses `HttpURLConnection`). This scheme-handler is registered by default

### OkHttpNetworkSchemeHandler
`OkHttpNetworkSchemeHandler` allows obtaining images from `http://` and `https://` uris
via [okhttp library](https://github.com/square/okhttp). Please note that in order to use
this scheme-handler you must explicitly add `okhttp` library to your project.

```java
// default instance
OkHttpNetworkSchemeHandler.create();

// specify OkHttpClient to use
OkHttpNetworkSchemeHandler.create(new OkHttpClient());

// @since 4.0.0
OkHttpNetworkSchemeHandler.create(Call.Factory);
```

### Custom SchemeHandler

```java
public abstract class SchemeHandler {

    @NonNull
    public abstract ImageItem handle(@NonNull String raw, @NonNull Uri uri);

    @NonNull
    public abstract Collection<String> supportedSchemes();
}
```

Starting with <Badge text="4.0.0" /> `SchemeHandler` can return a result (when no
further decoding is required):

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create(new ImagesPlugin.ImagesConfigure() {
            @Override
            public void configureImages(@NonNull ImagesPlugin plugin) {
                // for example to return a drawable resource
                plugin.addSchemeHandler(new SchemeHandler() {
                    @NonNull
                    @Override
                    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {

                        final int resourceId = context.getResources().getIdentifier(
                                raw.substring("resources://".length()),
                                "drawable",
                                context.getPackageName());

                        // it's fine if it throws, async-image-loader will catch exception
                        final Drawable drawable = context.getDrawable(resourceId);

                        // it's important to apply bounds to resulting drawable
                        DrawableUtils.applyIntrinsicBounds(drawable);
                        
                        return ImageItem.withResult(drawable);
                    }

                    @NonNull
                    @Override
                    public Collection<String> supportedSchemes() {
                        return Collections.singleton("resources");
                    }
                });
            }
        }))
```

:::tip
If you are using [html](/docs/v4/html/) you do not have to additionally setup
images displayed via `<img>` tag, as `HtmlPlugin` automatically uses configured
image loader. But images referenced in HTML come with additional support for
sizes, which is not supported natively by markdown, allowing absolute or relative sizes:

```html
<img src="./assets/my-image" width="100%">
```
:::