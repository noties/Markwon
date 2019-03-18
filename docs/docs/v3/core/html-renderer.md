# HTML Renderer

Starting with <Badge text="3.0.0" /> `MarkwonHtmlRenderer` controls how HTML
is rendered:

```java
Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureHtmlRenderer(@NonNull MarkwonHtmlRenderer.Builder builder) {
                builder.setHandler("a", new MyTagHandler());
            }
        });
```

:::danger
Customizing `MarkwonHtmlRenderer` is not enough to include HTML content in your application.
You must explicitly include [markwon-html](/docs/v3/html/) artifact (includes HtmlParser) 
to your project and register `HtmlPlugin`:

```java
Markwon.builder(context)
        .usePlugin(HtmlPlugin.create())
```
:::

For example, to create an `<a>` HTML tag handler:

```java
builder.setHandler("a", new SimpleTagHandler() {
    @Override
    public Object getSpans(
            @NonNull MarkwonConfiguration configuration,
            @NonNull RenderProps renderProps,
            @NonNull HtmlTag tag) {
        return new LinkSpan(
                configuration.theme(), 
                tag.attributes().get("href"), 
                configuration.linkResolver());
    }
});
```

`SimpleTagHandler` can be used for simple cases when a tag does not require any special
handling (like visiting it's children)

:::tip
One can return `null` a single span or an array of spans from `getSpans` method
:::

For a more advanced usage `TagHandler` can be used directly:

```java
builder.setHandler("a", new TagHandler() {
    @Override
    public void handle(@NonNull MarkwonVisitor visitor, @NonNull MarkwonHtmlRenderer renderer, @NonNull HtmlTag tag) {
        
        // obtain default spanFactory for Link node
        final SpanFactory factory = visitor.configuration().spansFactory().get(Link.class);
        
        if (factory != null) {
            
            // set destination property
            CoreProps.LINK_DESTINATION.set(
                    visitor.renderProps(), 
                    tag.attributes().get("href"));
            
            // Obtain spans from the factory
            final Object spans = factory.getSpans(
                    visitor.configuration(), 
                    visitor.renderProps());
            
            // apply spans to SpannableBuilder
            SpannableBuilder.setSpans(
                    visitor.builder(), 
                    spans, 
                    tag.start(), 
                    tag.end());
        }
    }
});
```

:::tip
Sometimes HTML content might include tags that are not closed (although 
they are required to be by the spec, for example a `div`).
Markwon by default disallows such tags and ignores them. Still,
there is an option to allow them _explicitly_ via builder method:
```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureHtmlRenderer(@NonNull MarkwonHtmlRenderer.Builder builder) {
                builder.allowNonClosedTags(true);
            }
        })
        .build();
```
Please note that if `allowNonClosedTags=true` then all non-closed tags will be closed
at the end of a document.
:::