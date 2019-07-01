# Strikethrough extension

<MavenBadge4 :artifact="'ext-strikethrough'" />

This module adds `strikethrough` functionality to `Markwon` via `StrikethroughPlugin`:

```java
Markwon.builder(context)
    .usePlugin(StrikethroughPlugin.create())
```

This plugin registers `SpanFactory` for `Strikethrough` node, so it's possible to customize Strikethrough Span that is used in rendering:

```java
Markwon.builder(context)
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                builder.setFactory(Strikethrough.class, new SpanFactory() {
                    @Override
                    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                        // will use Underline span instead of Strikethrough
                        return new UnderlineSpan();
                    }
                });
            }
        })
```
