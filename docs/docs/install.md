---
prev: false
next: /docs/getting-started.md
---

# Installation

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
implementation 'ru.noties:markwon:1.1.1-SNAPSHOT'
```

Please note that `markwon-image-loader`, `markwon-syntax-highlight` 
and `markwon-view` are also present in `SNAPSHOT` repository and 
share the same version as main `markwon` artifact.

