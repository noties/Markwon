package io.noties.markwon.app.sample.ui.adapt

import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.markwon.Markwon
import io.noties.markwon.app.R
import io.noties.markwon.app.sample.Sample
import io.noties.markwon.app.utils.displayName
import io.noties.markwon.app.utils.hidden
import io.noties.markwon.app.utils.tagDisplayName
import io.noties.markwon.app.widget.FlowLayout
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.utils.NoCopySpannableFactory

class SampleItem(
        private val markwon: Markwon,
        private val sample: Sample,
        private val onArtifactClick: (MarkwonArtifact) -> Unit,
        private val onTagClick: (String) -> Unit,
        private val onSampleClick: (Sample) -> Unit
) : Item<SampleItem.Holder>(sample.id.hashCode().toLong()) {

//    var search: String? = null

    private val text: Spanned by lazy(LazyThreadSafetyMode.NONE) {
        markwon.toMarkdown(sample.description)
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.adapt_sample, parent, false)).apply {
            description.setSpannableFactory(NoCopySpannableFactory.getInstance())
        }
    }

    override fun render(holder: Holder) {
        holder.apply {
            title.text = sample.title

            val text = this@SampleItem.text
            if (text.isEmpty()) {
                description.text = ""
                description.hidden = true
            } else {
                markwon.setParsedMarkdown(description, text)
                description.hidden = false
            }

            // there is no need to display the core artifact (it is implicit),
            //  hide if empty (removed core)
            artifacts.ensure(sample.artifacts.size, R.layout.view_artifact)
                    .zip(sample.artifacts)
                    .forEach { (view, artifact) ->
                        (view as TextView).text = artifact.displayName
                        view.setOnClickListener {
                            onArtifactClick(artifact)
                        }
                    }

            tags.ensure(sample.tags.size, R.layout.view_tag)
                    .zip(sample.tags)
                    .forEach { (view, tag) ->
                        (view as TextView).text = tag.tagDisplayName
                        view.setOnClickListener {
                            onTagClick(tag)
                        }
                    }

            itemView.setOnClickListener {
                onSampleClick(sample)
            }
        }
    }

    class Holder(itemView: View) : Item.Holder(itemView) {
        val title: TextView = requireView(R.id.title)
        val description: TextView = requireView(R.id.description)
        val artifacts: FlowLayout = requireView(R.id.artifacts)
        val tags: FlowLayout = requireView(R.id.tags)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SampleItem

        if (sample != other.sample) return false

        return true
    }

    override fun hashCode(): Int {
        return sample.hashCode()
    }
}

private fun FlowLayout.ensure(viewsCount: Int, layoutResId: Int): List<View> {
    if (viewsCount > childCount) {
        // inflate new views
        val inflater = LayoutInflater.from(context)
        for (i in 0 until (viewsCount - childCount)) {
            addView(inflater.inflate(layoutResId, this, false))
        }
    } else {
        // return requested vies and GONE the rest
        for (i in viewsCount until childCount) {
            getChildAt(i).hidden = true
        }
    }
    return (0 until viewsCount).map { getChildAt(it) }
}
