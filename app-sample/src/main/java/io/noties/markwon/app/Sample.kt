package io.noties.markwon.app

import android.os.Parcelable
import io.noties.markwon.sample.annotations.MarkwonArtifact
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Sample(
        val javaClassName: String,
        val id: String,
        val title: String,
        val description: String,
        val artifacts: List<MarkwonArtifact>,
        val tags: List<String>
) : Parcelable