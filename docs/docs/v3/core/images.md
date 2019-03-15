# Images

Starting with <Badge text="3.0.0" /> `Markwon` comes with `ImagesPlugin`
which supports `http(s)`, `file` and `data` schemes and default media
decoder (for simple images, no [SVG](/docs/v3/image/svg.md) or [GIF](/docs/v3/image/gif.md) which
are defined in standalone modules).

## ImagesPlugin

`ImagePlugin` takes care of _obtaining_ image resource, decoding it and displaying it in a `TextView`.

:::warning
Although `core` artifact contains `ImagesPlugin` one must 
still **explicitly** register the `ImagesPlugin` on resulting `Markwon`
instance.
```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create())
```
:::

There are 2 factory methods to obtain `ImagesPlugin`:
* `ImagesPlugin#create(Context)`
* `ImagesPlugin#createWithAssets(Context)`

The first one `#create(Context)` configures:
* `FileSchemeHandler` that allows obtaining images from `file://` uris
* `DataUriSchemeHandler` that allows _inlining_ images with `data:` 
  scheme (`data:image/svg+xml;base64,MTIz`)
* `NetworkSchemeHandler` that allows obtaining images from `http://` and `https://` uris
  (internally it uses `HttpURLConnection`)
* `ImageMediaDecoder` which _tries_ to decode all encountered images as regular ones (png, jpg, etc)

The second one `#createWithAssets(Context)` does the same but also adds support
for images that reside in `assets` folder of your application and
referenced by `file:///android_asset/{path}` uri.

`ImagesPlugin` also _prepares_ a TextView to display images. Due to asynchronous
nature of image loading, there must be a way to invalidate resulting Spanned 
content after an image is loaded.

:::warning
Images come with few limitations. For of all, they work with a **TextView only**.
This is due to the fact that there is no way to invalidate a `Spanned` content
by itself (without context in which it is displayed). So, if `Markwon` is used,
for example, to display a `Toast` with an image:

```java
final Spanned spanned = markwon.toMarkdown("Hello ![alt](https://my.image/1.JPG)");
Toast.makeText(context, spanned, Toast.LENGTH_LONG).show();
```

Image _probably_ won't be displayed. As a workaround for `Toast` a custom `View`
can be used:

```java
final Spanned spanned = markwon.toMarkdown("Hello ![alt](https://my.image/1.JPG)");

final View view = createToastView();
final TextView textView = view.findViewById(R.id.text_view);
markwon.setParsedMarkdown(textView, spanned);

final Toast toast = new Toast(context);
toast.setView(view);
// other Toast configurations
toast.show();
```
:::

## SchemeHandler

To add support for different schemes (or customize provided) a `SchemeHandler` must be used.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create(context))
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {
                // example only, Markwon doesn't come with a ftp scheme handler
                builder.addSchemeHandler("ftp", new FtpSchemeHandler());
            }
        })
        .build();
```

It's a class to _convert_ an URI into an `InputStream`:

```java
public abstract class SchemeHandler {

    @Nullable
    public abstract ImageItem handle(@NonNull String raw, @NonNull Uri uri);
}
```

`ImageItem` is a holder class for resulting `InputStream` and (optional)
content type:

```java
public class ImageItem {

    private final String contentType;
    private final InputStream inputStream;

    /* rest omitted */
}
```

Based on `contentType` returned a corresponding `MediaDecoder` will be matched.
If no `MediaDecoder` can handle given `contentType` then a default media decoder will
be used.

## MediaDecoder

By default `core` artifact comes with _default image decoder_ only. It's called
`ImageMediaDecoder` and it can decode all the formats that `BitmapFactory#decodeStream(InputStream)`
can.

```java
final Markwon markwon = Markwon.builder(this)
        .usePlugin(ImagesPlugin.create(this))
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {
                builder.addMediaDecoder("text/plain", new TextPlainMediaDecoder());
            }
        })
        .build();

```

`MediaDecoder` is a class to turn `InputStream` into a `Drawable`:

```java
public abstract class MediaDecoder {

    @Nullable
    public abstract Drawable decode(@NonNull InputStream inputStream);
}
```

:::tip
If you want to display GIF or SVG images also, you can use [image-gif](/docs/v3/image/gif.md)
and [image-svg](/docs/v3/image/svg.md) modules.
:::

:::tip
If you are using [html](/docs/v3/html/) you do not have to additionally setup
images displayed via `<img>` tag, as `HtmlPlugin` automatically uses configured
image loader. But images referenced in HTML come with additional support for
sizes, which is not supported natively by markdown, allowing absolute or relative sizes:

```html
<img src="./assets/my-image" width="100%">
```
:::

## Placeholder drawable <Badge text="3.0.0" />

It's possible to provide a custom placeholder for an image (whilst it's loading).

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create(context))
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {
                builder.placeholderDrawableProvider(new AsyncDrawableLoader.DrawableProvider() {
                    @Override
                    public Drawable provide() {
                        // your custom placeholder drawable
                        return new PlaceholderDrawable();
                    }
                });
            }
        });
```

## Error drawable <Badge text="3.0.0" />

To fallback in case of error whilst loading an image, an `error drawable` can be used:


```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create(context))
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {
                builder.errorDrawableProvider(new AsyncDrawableLoader.DrawableProvider() {
                    @Override
                    public Drawable provide() {
                        // your custom error drawable
                        return new MyErrorDrawable();
                    }
                });
            }
        });
```

:::warning
Before `3.0.0` `AsyncDrawableLoader` accepted a simple `Drawable` as error drawable
argument. Starting `3.0.0` it accepts a `DrawableProvider` instead.
:::
