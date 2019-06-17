# Installation

<LegacyWarning />

<MavenBadges2xx />

In order to start using `Markwon` add this to your dependencies block
in your projects `build.gradle`:

```groovy
implementation "ru.noties:markwon:${markwonVersion}"
```

This is core artifact that is sufficient to start displaying markdown in your Android applications.

`Markwon` comes with more artifacts that cover additional functionality, but they are
**not** required to be used, as most of them provide implementations for functionality
that is _interfaced_ in the core artifact

```groovy
implementation "ru.noties:markwon-image-loader:${markwonVersion}"
implementation "ru.noties:markwon-syntax-highlight:${markwonVersion}"
implementation "ru.noties:markwon-view:${markwonVersion}"
```

These artifacts share the same _version_ as the core artifact

### Image loader

```groovy
implementation "ru.noties:markwon-image-loader:${markwonVersion}"
```

Provides implementation of `AsyncDrawable.Loader` and comes with support for:
* SVG
* GIF
* Other image formats

Please refer to documentation for [image loader](/docs/v2/image-loader.md) module

### Syntax highlight

```groovy
implementation "ru.noties:markwon-syntax-highlight:${markwonVersion}"
```

Provides implementation of `SyntaxHighlight` and allows various syntax highlighting
in your markdown based Android applications. Comes with 2 ready-to-be-used themes: `light` and `dark`.
Please refer to documentation for [syntax highlight](/docs/v2/syntax-highlight.md) module

### View

```groovy
implementation "ru.noties:markwon-view:${markwonVersion}"
```

Provides 2 widgets to display markdown: `MarkwonView` and `MarkwonViewCompat` (subclasses
of `TextView` and `AppCompatTextView` respectively).
Please refer to documentation for [view](/docs/v2/view.md) module

## Proguard

When using `markwon-image-loader` artifact and Proguard is enabled, add these rules
to your proguard configuration:

```proguard
-dontwarn okhttp3.**
-dontwarn okio.**

-keep class com.caverock.androidsvg.** { *; }
-dontwarn com.caverock.androidsvg.**
```

They come from dependencies that `markwon-image-loader` is using.

:::tip Other artifacts
Other artifacts do not require special Proguard rules
:::

## Snapshot

![markwon-snapshot](https://img.shields.io/nexus/s/https/oss.sonatype.org/ru.noties/markwon.svg?label=markwon)

In order to use latest `SNAPSHOT` version add snapshot repository 
to your root project's `build.gradle` file:

```groovy
allprojects {
    repositories {
        jcenter()
        google()
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
    }
}
```

and then in your module `build.gradle`:

```groovy
implementation "ru.noties:markwon:${markwonSnapshotVersion}"
```

Please note that `markwon-image-loader`, `markwon-syntax-highlight` 
and `markwon-view` are also present in `SNAPSHOT` repository and 
share the same version as main `markwon` artifact.

