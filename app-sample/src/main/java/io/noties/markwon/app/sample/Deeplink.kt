package io.noties.markwon.app.sample

import android.net.Uri
import io.noties.debug.Debug
import io.noties.markwon.app.BuildConfig
import io.noties.markwon.sample.annotations.MarkwonArtifact

sealed class Deeplink {
    data class Sample(val id: String) : Deeplink()
    data class Search(val search: SampleSearch) : Deeplink()

    companion object {
        fun parse(data: Uri?): Deeplink? {
            Debug.i(data)
            @Suppress("NAME_SHADOWING")
            val data = data ?: return null
            if (BuildConfig.DEEPLINK_SCHEME != data.scheme) return null
            return when (data.host) {
                // markwon://sample/20202827
                "sample" -> parseSample(data.lastPathSegment)
                // markwon://search?a=ext-latex&q=text
                "search" -> parseSearch(data.query)
                else -> null
            }
        }

        private fun parseSample(id: String?): Sample? {
            if (id == null) return null
            return Sample(id)
        }

        private fun parseSearch(query: String?): Search? {
            Debug.i("query: '$query'")

            val params = query
                    ?.split("&")
                    ?.map {
                        val (k, v) = it.split("=")
                        Pair(k, v)
                    }
                    ?.toMap()
                    ?: return null

            val artifact = params["a"]
            val tag = params["t"]
            val search = params["q"]

            Debug.i("artifact: '$artifact', tag: '$tag', search: '$search'")

            val sampleSearch: SampleSearch? = if (artifact != null) {
                val encodedArtifact = MarkwonArtifact.values()
                        .firstOrNull { it.artifactName() == artifact }
                if (encodedArtifact != null) {
                    SampleSearch.Artifact(search, encodedArtifact)
                } else {
                    null
                }
            } else if (tag != null) {
                SampleSearch.Tag(search, tag)
            } else if (search != null) {
                SampleSearch.All(search)
            } else {
                null
            }

            if (sampleSearch == null) {
                return null
            }

            return Search(sampleSearch)
        }
    }
}