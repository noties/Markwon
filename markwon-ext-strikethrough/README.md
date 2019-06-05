# Strikethrough

![stable](https://img.shields.io/maven-central/v/io.noties.markwon/ext-strikethrough.svg)
![snapshot](https://img.shields.io/nexus/s/https/oss.sonatype.org/io.noties.markwon/ext-strikethrough.svg)

```kotlin
implementation "io.noties.markwon:ext-strikethrough:${markwonVersion}"
```


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