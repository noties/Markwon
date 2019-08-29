# RenderProps <Badge text="3.0.0" />

<LegacyWarning />

`RenderProps` encapsulates passing arguments from a node visitor to a node renderer.
Without hardcoding arguments into an API method calls.

`RenderProps` is the state collection for `Props` that are set by a node visitor and
retrieved by a node renderer.

```java
public class Prop<T> {

    @NonNull
    public static <T> Prop<T> of(@NonNull String name) {
        return new Prop<>(name);
    }

    /* ... */
}
```

For example `CorePlugin` defines a _Heading level_ prop (inside `CoreProps` class):

```java
public static final Prop<Integer> HEADING_LEVEL = Prop.of("heading-level");
```

Then CorePlugin registers a `Heading` node visitor and applies heading value:

```java
@Override
public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
    builder.on(Heading.class, new MarkwonVisitor.NodeVisitor<Heading>() {
        @Override
        public void visit(@NonNull MarkwonVisitor visitor, @NonNull Heading heading) {
            
            /* Heading node handling logic */

            // set heading level
            CoreProps.HEADING_LEVEL.set(visitor.renderProps(), heading.getLevel());
            
            // a helper method to apply span(s) for a node 
            // (internally obtains a SpanFactory for Heading or silently ignores
            // this call if no factory for a Heading is registered)
            visitor.setSpansForNodeOptional(heading, start);

            /* Heading node handling logic */
        }
    });
}
```

And finally `HeadingSpanFactory` (which is also registered by `CorePlugin`):

```java
public class HeadingSpanFactory implements SpanFactory {
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return new HeadingSpan(
                configuration.theme(),
                CoreProps.HEADING_LEVEL.require(props)
        );
    }
}
```

---

`Prop<T>` has these methods:

* `@Nullable T get(RenderProps)` - returns value stored in RenderProps or `null` if none is present
* `@NonNull T get(RenderProps, @NonNull T defValue)` - returns value stored in RenderProps or default value (this method always return non-null value)
* `@NonNull T require(RenderProps)` - returns value stored in RenderProps or _throws an exception_ if none is present
* `void set(RenderProps, @Nullable T value)` - updates value stored in RenderProps, passing `null` as value is the same as calling `clear`
* `void clear(RenderProps)` - clears value stored in RenderProps
