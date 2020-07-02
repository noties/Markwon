# Markwon sample app

## Building

When adding/removing samples _most likely_ a clean build would be required.
First, for annotation processor to create `samples.json`. And secondly,
in order for Android Gradle plugin to bundle resources references via
symbolic links (the `sample.json` itself and `io.noties.markwon.app.samples.*` directory)

```gradle
./gradlew :app-s:clean :app-s:asDe
```