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
            return when (data.scheme) {
                // local deeplink with custom scheme (`markwon://`)
                BuildConfig.DEEPLINK_SCHEME -> {
                    when (data.host) {
                        "sample" -> parseSample(data.lastPathSegment)
                        "search" -> parseSearch(data.query)
                        else -> null
                    }
                }
                // https deeplink, `https://noties.io/Markwon/sample`
                "https" -> {
                    // https://noties.io/Markwon/app/sample/ID
                    // https://noties.io/Markwon/app/search?a=core
                    val segments = data.pathSegments
                    if (segments.size == 3
                            && "Markwon" == segments[0]
                            && "app" == segments[1]) {
                        when (segments[2]) {
                            "sample" -> parseSample(data.lastPathSegment)
                            "search" -> parseSearch(data.query)
                            else -> null
                        }
                    } else {
                        null
                    }
                }
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
                    ?.let {
                        // `https:.*` has query with `search?a=core`
                        val index = it.indexOf('?')
                        if (index > -1) {
                            it.substring(index + 1)
                        } else {
                            it
                        }
                    }
                    ?.split("&")
                    ?.map {
                        val (k, v) = it.split("=")
                        Pair(k, v)
                    }
                    ?.toMap()
                    ?: return null

            Debug.i("params: $params")

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