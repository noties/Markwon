# Registry <Badge text="4.0.0" />

`Registry` allows to pre-configure other plugins and/or declare a dependency on a plugin,
which also will modify internal order of plugins inside a `Markwon` instance.

For example, you have a configurable plugin:

```java
public class MyPlugin extends AbstractMarkwonPlugin {
    
    private boolean enabled;

    public boolean enabled() {
        return enabled;
    }

    @NonNull
    public MyPlugin enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    {...}
}
```

and other plugin that needs to access `MyPlugin` or modify/configure it:

```java
public class MyOtherPlugin extends AbstractMarkwonPlugin {
    @Override
    public void configure(@NonNull Registry registry) {
        registry.require(MyPlugin.class, new Action<MyPlugin>() {
            @Override
            public void apply(@NonNull MyPlugin myPlugin) {
                myPlugin.enabled(false);
            }
        });
    }
}
```

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new MyOtherPlugin())
        .usePlugin(new MyPlugin())
        .build();
```

_Internal_ plugins order (in this case) will be:
* `CorePlugin` (added automatically and always the first one)
* `MyPlugin` (was required by `MyOtherPlugin`)
* `MyOtherPlugin`

:::tip
There is no need to _require_ `CorePlugin` as it will be the first one inside
`Markwon` instance.
:::

The order matters if you want to _override_ some plugin. For example, `CoolPlugin`
adds a `SpanFactory` for a `Cool` markdown node. Other `NotCoolPlugin` wants to
use a different `SpanFactory`, then:

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(CoolPlugin.create())
        .usePlugin(new NotCoolPlugin() {
            
            @Override
            public void configure(@NonNull MarkwonPlugin.Registry registry) {
                registry.require(CoolPlugin.class);
            }

            @Override
            public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                builder.setFactory(Cool.class, new NotCoolSpanFactory());
            }
        })
        .build();
```

---

All `require` calls to the `Registry` will also validate at runtime that
_required_ plugins are registered.

```java
final Markwon markwon = Markwon.builder(context)
        .usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configure(@NonNull Registry registry) {
                // will throw an exception if `NotPresentPlugin` is not present
                registry.require(NotPresentPlugin.class);
            }
        })
        .build();
```