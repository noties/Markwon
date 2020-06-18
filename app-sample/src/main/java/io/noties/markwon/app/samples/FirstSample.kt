package io.noties.markwon.app.samples

import io.noties.markwon.app.ui.MarkwonSample
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo

@MarkwonSampleInfo(
        id = "202006164150023",
        title = "First Sample",
        description = "This **is** _the first_ sample",
        artifacts = [MarkwonArtifact.CORE],
        tags = ["test"]
)
class FirstSample : MarkwonSample() {
}