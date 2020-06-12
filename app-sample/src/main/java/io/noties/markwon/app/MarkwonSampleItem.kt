package io.noties.markwon.app

import io.noties.markwon.sample.annotations.MarkwonArtifact

data class MarkwonSampleItem(
        val javaClassName: String,
        val id: String,
        val title: String,
        val description: String,
        val artifacts: List<MarkwonArtifact>,
        val tags: List<String>
)