---
prev: false
next: /docs/v4/core/getting-started.md
---

# Installation

<table>
    <tbody>
        <tr>
            <td><img alt="stable" src="https://img.shields.io/maven-central/v/io.noties.markwon/core.svg?label=stable"></td>
            <td><a href="https://github.com/noties/Markwon/blob/master/CHANGELOG.md">changelog<OutboundLink/></a></td>
        </tr>
        <tr>
            <td><img alt="snapshot" src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.noties.markwon/core.svg?label=snapshot"></td>
            <td><a href="https://github.com/noties/Markwon/blob/develop/CHANGELOG.md">changelog<OutboundLink/></a></td>
        </tr>
    </tbody>
</table>

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

