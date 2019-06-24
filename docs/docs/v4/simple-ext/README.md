# Simple Extension <Badge text="4.0.0" />

<MavenBadge4 :artifact="'simple-ext'" />

`SimpleExtPlugin` allows creating simple _delimited_ extensions, for example:

```md
+this is text surrounded by `+`+
```

```java
final Markwon markwon = Markwon.builder(this)
        .usePlugin(SimpleExtPlugin.create(plugin -> plugin
                // +sometext+
                .addExtension(1, '+', new SpanFactory() {
                    @Override
                    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                        return new EmphasisSpan();
                    }
                })
        .build();
```

or

```java
final Markwon markwon = Markwon.builder(this)
        .usePlugin(SimpleExtPlugin.create())
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configure(@NonNull Registry registry) {
                registry.require(SimpleExtPlugin.class, new Action<SimpleExtPlugin>() {
                    @Override
                    public void apply(@NonNull SimpleExtPlugin plugin) {
                        plugin.addExtension(1, '+', new SpanFactory() {
                            @Override
                            public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                                return new EmphasisSpan();
                            }
                        })
                    }
                });
            }
        })
        .build();
```

If opening and closing characters are different another method can be used:

```java
plugin.addExtension(
    /*length*/2, 
    /*openingCharacter*/'@', 
    /*closingCharacter*/'$', 
    /*spanFactory*/(configuration, props) -> new ForegroundColorSpan(Color.RED))))
```

This extension will be applied to a text like this:

```md
@@we are inside different delimiter characters$$
```
