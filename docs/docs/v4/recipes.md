# Recipes


## SpannableFactory

Consider using `NoCopySpannableFactory` when a `TextView` will be used to display markdown
multiple times (for example in a `RecyclerView`):

```java
// call after inflation and before setting markdown
textView.setSpannableFactory(NoCopySpannableFactory.getInstance());
```


## Autolink

Do not use `autolink` XML attribute on your `TextView` as it will remove all links except autolinked ones.
Consider using [linkify plugin](/docs/v4/linkify/) or commonmark-java [autolink extension](https://github.com/atlassian/commonmark-java)



## Custom typeface

When using a custom typeface on a `TextView` you might find that **bold** and *italic* nodes
are displayed incorrectly. Consider registering own `SpanFactories` for `StrongEmphasis` and `Emphasis` nodes:

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                builder
                        .setFactory(StrongEmphasis.class, new SpanFactory() {
                            @Override
                            public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                                return new StyleSpan(Typeface.BOLD);
                            }
                        })
                        .setFactory(Emphasis.class, new SpanFactory() {
                            @Override
                            public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                                return new StyleSpan(Typeface.ITALIC);
                            }
                        });
            }
        })
        .build();
```

Please check that `StyleSpan` works for you. If it doesn't consider 
using `CustomTypefaceSpan` with your typeface directly.


