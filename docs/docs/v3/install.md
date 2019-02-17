---
prev: false
next: /docs/v3/core/getting-started.md
---

# Installation

![release](https://img.shields.io/maven-central/v/ru.noties.markwon/core.svg?label=release)
![snapshot](https://img.shields.io/nexus/s/https/oss.sonatype.org/ru.noties.markwon/core.svg?label=snapshot)

<ArtifactPicker />

# Bundle <Badge text="3.0.0" />
If you wish to include all Markwon artifacts or add specific artifacts 
in a different manner than explicit gradle dependency definition, you can 
use `markwon-bundle.gradle` gradle script:

*(in your `build.gradle`)*
```groovy
apply plugin: 'com.android.application'
apply from: 'https://raw.githubusercontent.com/noties/Markwon/master/markwon-bundle.gradle'

android { /* */ }

ext.markwon = [
    'version': '3.0.0',
    'includeAll': true
]

dependencies { /* */ }
```

`markwon` object can have these properties:
* `version` - (required) version of `Markwon`
* `includeAll` - if _true_ will add all known Markwon artifacts. Can be used with `exclude`
* * `exclude` - an array of artifacts to _exclude_ (cannot exclude `core`)
* `artifacts` - an array of artifacts (can omit `core`, as it will be added implicitly anyway)

If `includeAll` property is present and is `true`, then `artifacts` property won't be used.
If there is no `includeAll` property or if it is `false`, `exclude` property won't be used.

These 2 markwon objects are equal:

```groovy
// #1
ext.markwon = [
    'version': '3.0.0',
    'artifacts': [
        'ext-latex',
        'ext-strikethrough',
        'ext-tables',
        'ext-tasklist',
        'html',
        'image-gif',
        'image-okhttp',
        'image-svg',
        'recycler',
        'syntax-highlight'
    ]
]

// #2
ext.markwon = [
    'version': '3.0.0',
    'includeAll': true
]
```

## Snapshot

In order to use latest `SNAPSHOT` version add snapshot repository 
to your root project's `build.gradle` file:

```groovy
allprojects {
    repositories {
        jcenter()
        google()
        // this one 👇
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' } // 👈 this one
        // this one 👆
    }
}
```

:::tip Info
All official artifacts share the same version number and all 
are uploaded to **release** and **snapshot** repositories
:::

