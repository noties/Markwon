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

All possible arguments are passed via [RenderProps](/docs/v4/core/render-props.md):

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
Or an array of spans (you _can_ go deeper):

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

---

:::warning
Deprecated in <Badge text="4.2.2" type="error" vertical="middle" />. Use `appendFactory` or `prependFactory` for
more explicit factories ordering. `addFactories` behaves like new `prependFactory` method.
:::

Since <Badge text="3.0.1" /><Badge text="4.2.2" type="error" /> you can _add_ multiple `SpanFactory` for a single node:

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                // this factory will be used _along_ with all other factories for specified node
                builder.addFactory(Code.class, new SpanFactory() {
                    @Override
                    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                        return new ForegroundColorSpan(Color.GREEN);
                    }
                });
            }
        })
        .build();
```

## appendFactory/prependFactory <Badge text="4.2.2" />

* use `appendFactory` if you wish to add a factory **after** original (can be used to post-process original factory)
* use `prependFactory` if you wish to add a factory **before** original (original factory will be applied after this one)

```java
@Override
public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
    // `RemoveUnderlineSpan` will be added AFTER original, thus it will remove underline applied by original
    builder.appendFactory(Link.class, (configuration, props) -> new RemoveUnderlineSpan());

    // will be added BEFORE origin (origin can override this)
    builder.prependFactory(Link.class, (configuration, props) -> new AbsoluteSizeSpan(48, true));
}
```

---

If you wish to inspect existing factory you can use:
* `builder#getFactory()` -> returns registered factory or `null`
* `builder#requireFactory()` -> returns registered factory or throws <Badge text="3.0.1" />

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                final SpanFactory codeFactory = builder.requireFactory(Code.class);
                final SpanFactory linkFactory = builder.getFactory(Link.class);
                if (linkFactory != null) {
                    {...}
                }
            }
        })
        .build();
```