package io.noties.markwon.app.adapt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.markwon.sample.annotations.MarkwonArtifact

class ArtifactItem(artifact: MarkwonArtifact): Item<ArtifactItem.Holder>(artifact.name.hashCode().toLong()) {

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(0, parent, false))
    }

    override fun render(holder: Holder) {
    }

    class Holder(itemView: View): Item.Holder(itemView) {
        val textView: TextView = requireView(0)
    }
}