# Image SVG

<LegacyWarning />

<MavenBadge :artifact="'image-svg'" />

Adds support for SVG images inside markdown. 
Relies on [androidsvg library](https://github.com/BigBadaboom/androidsvg)

```java
final Markwon markwon = Markwon.builder(context)
        // it's required to register ImagesPlugin
        .usePlugin(ImagesPlugin.create(context))
        .usePlugin(SvgPlugin.create(context.getResources()))
        .build();
```

:::tip
`SvgPlugin` requires `Resources` in order to scale SVG media based on display density
:::

## Proguard

```proguard
-keep class com.caverock.androidsvg.** { *; }
-dontwarn com.caverock.androidsvg.**
```