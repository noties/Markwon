# Visitor

Starting with <Badge text="3.0.0" /> _visiting_ of parsed markdown
nodes does not require creating own instance of commonmark-java `Visitor`,
instead a composable/configurable `MarkwonVisitor` is used.

## Visitor.Builder
There is no need to create own instance of `MarkwonVisitor.Builder` as
it is done by `Markwon` itself. One still can configure it as one wishes:

```java
final Markwon markwon = Markwon.builder(contex)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                builder.on(SoftLineBreak.class, new MarkwonVisitor.NodeVisitor<SoftLineBreak>() {
                    @Override
                    public void visit(@NonNull MarkwonVisitor visitor, @NonNull SoftLineBreak softLineBreak) {
                        visitor.forceNewLine();
                    }
                });
            }
        });
```

---

`MarkwonVisitor` encapsulates most of the functionality of rendering parsed markdown.

It holds rendering configuration:
* `MarkwonVisitor#configuration` - getter for current [MarkwonConfiguration](/docs/v4/core/configuration.md)
* `MarkwonVisitor#renderProps` - getter for current [RenderProps](/docs/v4/core/render-props.md)
* `MarkwonVisitor#builder` - getter for current `SpannableBuilder`

It contains also a number of utility functions:
* `visitChildren(Node)` - will visit all children of supplied Node
* `hasNext(Node)` - utility function to check if supplied Node has a Node after it (useful for white-space management, so there should be no blank new line after last BlockNode)
* `ensureNewLine` - will insert a new line at current `SpannableBuilder` position only if current (last) character is not a new-line
* `forceNewLine` - will insert a new line character without any condition checking
* `length` - helper function to call `visitor.builder().length()`, returns current length of `SpannableBuilder`
* `clear` - will clear state for `RenderProps` and `SpannableBuilder`, this is done by `Markwon` automatically after each render call

And some utility functions to control the spans:
* `setSpans(int start, Object spans)` - will apply supplied `spans` on `SpannableBuilder` starting at `start` position and ending at `SpannableBuilder#length`. `spans` can be `null` (no spans will be applied) or an array of spans (each span of this array will be applied)
* `setSpansForNodeOptional(N node, int start)` - helper method to set spans for specified `node` (internally obtains `SpanFactory` for that node and uses it to apply spans)
* `setSpansForNode(N node, int start)` - almost the same as `setSpansForNodeOptional` but instead of silently ignoring call if none `SpanFactory` is registered, this method will throw an exception.

```java
@Override
public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
    builder.on(Heading.class, new MarkwonVisitor.NodeVisitor<Heading>() {
        @Override
        public void visit(@NonNull MarkwonVisitor visitor, @NonNull Heading heading) {

            // or just `visitor.length()`
            final int start = visitor.builder().length();

            visitor.visitChildren(heading);

            // or just `visitor.setSpansForNodeOptional(heading, start)`
            final SpanFactory factory = visitor.configuration().spansFactory().get(heading.getClass());
            if (factory != null) {
                visitor.setSpans(start, factory.getSpans(visitor.configuration(), visitor.renderProps()));
            }
            
            if (visitor.hasNext(heading)) {
                visitor.ensureNewLine();
                visitor.forceNewLine();
            }
        }
    });
}
```

### BlockHandler <Badge text="4.3.0" />

Since <Badge text="4.3.0" /> there is class to control insertions of new lines after markdown blocks
`BlockHandler` (`MarkwonVisitor.BlockHandler`) and its default implementation `BlockHandlerDef`. For example,
to disable an empty new line after `Heading`:

```java
final Markwon markwon = Markwon.builder(this)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                builder.blockHandler(new BlockHandlerDef() {
                    @Override
                    public void blockEnd(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
                        if (node instanceof Heading) {
                            if (visitor.hasNext(node)) {
                                visitor.ensureNewLine();
                                // ensure new line but do not force insert one
                            }
                        } else {
                            super.blockEnd(visitor, node);
                        }
                    }
                });
            }
        })
        .build();
```