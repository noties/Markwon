package io.noties.markwon.app.utils

import io.noties.markwon.sample.annotations.MarkwonArtifact

val MarkwonArtifact.displayName: String
    get() = "@${artifactName()}"

val String.tagDisplayName: String
    get() = "#$this"