---
prev: false
next: /docs/core/getting-started.md
---

# Installation

![release](https://img.shields.io/maven-central/v/ru.noties.markwon/core.svg?label=release)
![snapshot](https://img.shields.io/nexus/s/https/oss.sonatype.org/ru.noties.markwon/core.svg?label=snapshot)

<ArtifactPicker />

## Snapshot

In order to use latest `SNAPSHOT` version add snapshot repository 
to your root project's `build.gradle` file:

```groovy
allprojects {
    repositories {
        jcenter()
        google()
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' } // <- this one
    }
}
```

and then in your module `build.gradle`:

```gradle
implementation "ru.noties.markwon:core:${markwonSnapshotVersion}"
```

:::tip
All official artifacts share the same version number and all 
are uploaded to **release** and **snapshot** repositories
:::

