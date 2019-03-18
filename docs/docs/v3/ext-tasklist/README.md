# Task list extension

<MavenBadge :artifact="'ext-tasklist'" />

Adds support for GFM (Github-flavored markdown) task-lists:

```java
Markwon.builder(context)
        .usePlugin(TaskListPlugin.create(context));
```

---

Create a default instance of `TaskListPlugin` with `TaskListDrawable` initialized to use
`android.R.attr.textColorLink` as primary color and `android.R.attr.colorBackground` as background
```java
TaskListPlugin.create(context);
```

---

Create an instance of `TaskListPlugin` with exact color values to use:
```java
// obtain color values
final int checkedFillColor = /* */;
final int normalOutlineColor = /* */;
final int checkMarkColor = /* */;

TaskListPlugin.create(checkedFillColor, normalOutlineColor, checkMarkColor);
```

---

Specify own drawable for a task list item:

```java
// obtain drawable
final Drawable drawable = /* */;

TaskListPlugin.create(drawable);
```

:::warning
Please note that custom drawable for a task list item must correctly handle state
in order to display done/not-done:

```java
public class MyTaskListDrawable extends Drawable {

    private boolean isChecked;

    @Override
    public void draw(@NonNull Canvas canvas) {
        // draw accordingly to the isChecked value
    }
    
    /* implementation omitted */

    @Override
    protected boolean onStateChange(int[] state) {
        final boolean isChecked = contains(state, android.R.attr.state_checked);
        final boolean result = this.isChecked != isChecked;
        if (result) {
            this.isChecked = isChecked;
        }
        return result;
    }

    private static boolean contains(@Nullable int[] states, int value) {
        if (states != null) {
            for (int state : states) {
                if (state == value) {
                    // NB return here
                    return true;
                }
            }
        }
        return false;
    }
}
```
:::

## Task list mutation

It is possible to mutate task list item state (toggle done/not-done). But note
that `Markwon` won't handle state change internally by any means and this change
is merely a visual one. If you need to persist state of a task list
item change you have to implement it yourself. This should get your started:

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(TaskListPlugin.create(context))
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {

                // obtain original SpanFactory set by TaskListPlugin
                final SpanFactory origin = builder.getFactory(TaskListItem.class);
                if (origin == null) {
                    // or throw, as it's a bit weird state and we expect
                    // this factory to be present
                    return;
                }

                builder.setFactory(TaskListItem.class, new SpanFactory() {
                    @Override
                    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                        // it's a bit non-secure behavior and we should validate
                        // the type of returned span first, but for the sake of brevity
                        // we skip this step
                        final TaskListSpan span = (TaskListSpan) origin.getSpans(configuration, props);

                        if (span == null) {
                            // or throw
                            return null;
                        }

                        // return an array of spans
                        return new Object[]{
                                span,
                                new ClickableSpan() {
                                    @Override
                                    public void onClick(@NonNull View widget) {
                                        // toggle VISUAL state
                                        span.setDone(!span.isDone());

                                        // do not forget to invalidate widget
                                        widget.invalidate();

                                        // execute your persistence logic
                                    }

                                    @Override
                                    public void updateDrawState(@NonNull TextPaint ds) {
                                        // no-op, so appearance is not changed (otherwise
                                        // task list item will look like a link)
                                    }
                                }
                        };
                    }
                });
            }
        })
        .build();
```