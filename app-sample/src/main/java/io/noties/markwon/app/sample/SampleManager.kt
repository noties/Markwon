package io.noties.markwon.app.sample

import android.content.Context
import io.noties.markwon.app.utils.Cancellable
import io.noties.markwon.app.utils.SampleUtils
import io.noties.markwon.sample.annotations.MarkwonArtifact
import java.util.concurrent.ExecutorService

class SampleManager(
        private val context: Context,
        private val executorService: ExecutorService
) {

    private val samples: List<Sample> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        SampleUtils.readSamples(context)
    }

    fun samples(search: SampleSearch?, callback: (List<Sample>) -> Unit): Cancellable {

        var action: ((List<Sample>) -> Unit)? = callback

        val future = executorService.submit {

            val source = when (search) {
                is SampleSearch.Artifact -> samples.filter { it.artifacts.contains(search.artifact) }
                is SampleSearch.Tag -> samples.filter { it.tags.contains(search.tag) }
                else -> samples.toList() // just copy all
            }

            val text = search?.text
            val results = if (text == null) {
                // no further filtering, just return the full source here
                source
            } else {
                source.filter { filter(it, text) }
            }

            action?.invoke(results)
        }

        return object : Cancellable {
            override val isCancelled: Boolean
                get() = future.isDone

            override fun cancel() {
                action = null
                future.cancel(true)
            }
        }
    }

    // if title contains,
    // if description contains,
    // if tags contains
    // if artifacts contains,
    private fun filter(sample: Sample, text: String): Boolean {
        return sample.javaClassName.contains(text, true)
                || sample.title.contains(text, true)
                || sample.description.contains(text, true)
                || filterTags(sample.tags, text)
                || filterArtifacts(sample.artifacts, text)
    }

    private fun filterTags(tags: List<String>, text: String): Boolean {
        return tags.firstOrNull { it.contains(text, true) } != null
    }

    private fun filterArtifacts(artifacts: List<MarkwonArtifact>, text: String): Boolean {
        return artifacts.firstOrNull { it.artifactName().contains(text, true) } != null
    }
}