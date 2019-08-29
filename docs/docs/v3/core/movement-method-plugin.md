# Movement method plugin

<LegacyWarning />

`MovementMethodPlugin` can be used to apply a `MovementMethod` to a TextView
(important if you have links inside your markdown). By default `CorePlugin`
will set a `LinkMovementMethod` on a TextView if one is missing. If you have
specific needs for a `MovementMethod` and `LinkMovementMethod` doesn't answer
your needs use `MovementMethodPlugin`:

```java
Markwon.builder(context)
        .usePlugin(MovementMethodPlugin.create(ScrollingMovementMethod.getInstance()))
```

:::tip
If you are having trouble with system `LinkMovementMethod` as an alternative
[BetterLinkMovementMethod](https://github.com/saket/Better-Link-Movement-Method) library can be used.
:::
