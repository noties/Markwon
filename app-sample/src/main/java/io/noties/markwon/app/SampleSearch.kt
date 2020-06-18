package io.noties.markwon.app

import io.noties.markwon.sample.annotations.MarkwonArtifact

sealed class SampleSearch(val text: String?) {
    class Artifact(text: String?, val artifact: MarkwonArtifact) : SampleSearch(text)
    class Tag(text: String?, val tag: String) : SampleSearch(text)
    class All(text: String?) : SampleSearch(text)

    override fun toString(): String {
        return "SampleSearch(text=$text,type=${javaClass.simpleName})"
    }
}