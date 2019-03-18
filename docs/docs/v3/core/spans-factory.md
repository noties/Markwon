# Spans Factory

Starting with <Badge text="3.0.0" /> `MarkwonSpansFactory` controls what spans are displayed
for markdown nodes.

```java
Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                // passing null as second argument will remove previously added 
                // factory for the Link node
                builder.setFactory(Link.class, null);
            }
        });
```

## SpanFactory

In order to create a _generic_ interface for all possible Nodes, a `SpanFactory`
was added:

```java
builder.setFactory(Link.class, new SpanFactory() {
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return null;
    }
});
```

All possible arguments are passed via [RenderProps](/docs/v3/core/render-props.md):

```java
builder.setFactory(Link.class, new SpanFactory() {
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        final String href = CoreProps.LINK_DESTINATION.require(props);
        return new LinkSpan(configuration.theme(), href, configuration.linkResolver());
    }
});
```

`SpanFactory` allows returning `null` for a certain span (no span will be applied).
Or an array of spans:

```java
builder.setFactory(Link.class, new SpanFactory() {
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return new Object[]{
                new LinkSpan(
                        configuration.theme(),
                        CoreProps.LINK_DESTINATION.require(props),
                        configuration.linkResolver()),
                new ForegroundColorSpan(Color.RED)
        };
    }
});
```