# Strikethrough

[![ext-strikethrough](https://img.shields.io/maven-central/v/io.noties.markwon/ext-strikethrough.svg?label=ext-strikethrough)](http://search.maven.org/#search|ga|1|g%3A%22io.noties.markwon%22%20AND%20a%3A%22ext-strikethrough%22)

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