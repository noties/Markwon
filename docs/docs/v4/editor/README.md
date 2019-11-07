# Editor <Badge text="4.2.0" />

<MavenBadge4 :artifact="'editor'" />

Markdown editing highlight for Android based on **Markwon**.

<style>
video {
    max-height: 82vh;
}
</style>

<video controls="true" loop="" :poster="$withBase('/assets/markwon-editor-preview.jpg')">
    <source :src="$withBase('/assets/markwon-editor.mp4')" type="video/mp4">
    You browser does not support mp4 playback, try downloading video file 
    <a :href="$withBase('/assets/markwon-editor.mp4')">directly</a>
</video>

## Getting started with editor

:::warning Implementation Detail
It must be mentioned that highlight is implemented via text diff. Everything
that is present in raw markdown input and missing from rendered result is considered
to be _punctuation_.
:::