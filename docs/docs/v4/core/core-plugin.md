# Core plugin <Badge text="3.0.0" />

Since <Badge text="3.0.0" /> with introduction of _plugins_, Markwon
**core** functionality was moved to a dedicated plugin.

```java
CorePlugin.create();
```

## Node visitors

`CorePlugin` registers these `commonmark-java` node visitors:
* `Text`
* `StrongEmphasis`
* `Emphasis`
* `BlockQuote`
* `Code`
* `Image`
* `FencedCodeBlock`
* `IndentedCodeBlock`
* `BulletList`
* `OrderedList`
* `ListItem`
* `ThematicBreak`
* `Heading`
* `SoftLineBreak`
* `HardLineBreak`
* `Paragraph`
* `Link`

## Span factories

`CorePlugin` adds these `SpanFactory`s:
* `StrongEmphasis`
* `Emphasis`
* `BlockQuote`
* `Code`
* `FencedCodeBlock`
* `IndentedCodeBlock`
* `ListItem`
* `Heading`
* `Link`
* `ThematicBreak`


:::tip
By default `CorePlugin` does not register a `Paragraph` `SpanFactory` but
this can be done in your custom plugin:

```java
Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                builder.setFactory(Paragraph.class, (configuration, props) -> 
                        new ForegroundColorSpan(Color.RED));
            }
        })
```
:::

## Props
These props are exported by `CorePlugin` and can be found in `CoreProps`:
* `Prop<ListItemType> LIST_ITEM_TYPE` (BULLET | ORDERED)
* `Prop<Integer> BULLET_LIST_ITEM_LEVEL`
* `Prop<Integer> ORDERED_LIST_ITEM_NUMBER`
* `Prop<Integer> HEADING_LEVEL`
* `Prop<String> LINK_DESTINATION`
* `Prop<Boolean> PARAGRAPH_IS_IN_TIGHT_LIST`

:::warning List item type
Before <Badge text="3.0.0" /> `Markwon` had 2 distinct lists (bullet and ordered). 
Since <Badge text="3.0.0" /> a single `SpanFactory` is used, which internally checks 
for `Prop<ListItemType> LIST_ITEM_TYPE`.
Beware of this if you would like to override only one of the list types. This is
done to correspond to `commonmark-java` implementation.
:::

More information about props can be found [here](/docs/v4/core/render-props.md)

---

:::tip Soft line break
Since <Badge text="3.0.0" /> Markwon core does not give an option to
insert a new line when there is a soft line break in markdown. Instead a
custom plugin can be used:

```java
final Markwon markwon = Markwon.builder(this)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                builder.on(SoftLineBreak.class, (visitor, softLineBreak) ->
                        visitor.forceNewLine());
            }
        })
        .build();
```
:::

:::warning
Please note that `CorePlugin` will implicitly set a `LinkMovementMethod` on a TextView
if one is not present. If you wish to customize a MovementMethod that is used, apply
one manually to a TextView (before applying markdown) or use the [MovementMethodPlugin](/docs/v4/core/movement-method-plugin.md)
which accepts a MovementMethod as an argument.
:::

## OnTextAddedListener <Badge text="4.0.0"/>

Since `4.0.0` `CorePlugin` provides ability to receive text-added event. This can
be useful in order to process raw text (for example to [linkify](/docs/v4/linkify.md) it):

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configure(@NonNull Registry registry) {
                registry.require(CorePlugin.class, new Action<CorePlugin>() {
                    @Override
                    public void apply(@NonNull CorePlugin corePlugin) {
                        corePlugin.addOnTextAddedListener(new CorePlugin.OnTextAddedListener() {
                            @Override
                            public void onTextAdded(@NonNull MarkwonVisitor visitor, @NonNull String text, int start) {
                                
                                // NB text is already added and you are __strongly__ adviced not to
                                // modify visitor here, but only add spans
                                //
                                // this will make all text BLUE
                                visitor.builder().setSpan(
                                        new ForegroundColorSpan(Color.BLUE),
                                        start,
                                        visitor.length()
                                );
                            }
                        });
                    }
                });
            }
        })
        .build();
```