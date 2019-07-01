---
prev: false
next: /docs/v4/core/getting-started.md
---

# Installation

![stable](https://img.shields.io/maven-central/v/io.noties.markwon/core.svg?label=stable)
![snapshot](https://img.shields.io/nexus/s/https/oss.sonatype.org/io.noties.markwon/core.svg?label=snapshot)

<ArtifactPicker4 />

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

