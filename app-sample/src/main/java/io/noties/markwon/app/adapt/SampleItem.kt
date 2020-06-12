package io.noties.markwon.app.adapt

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.debug.Debug
import io.noties.markwon.app.MarkwonSampleItem
import io.noties.markwon.app.R
import io.noties.markwon.sample.annotations.MarkwonArtifact

class SampleItem(private val item: MarkwonSampleItem) : Item<SampleItem.Holder>(item.id.hashCode().toLong()) {

    var search: String? = null

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        val holder = Holder(inflater.inflate(R.layout.adapt_sample, parent, false))
        holder.artifactsAndTags.movementMethod = LinkMovementMethod.getInstance()
        return holder
    }

    override fun render(holder: Holder) {
        holder.apply {
            title.text = item.title
            description.text = item.description
            artifactsAndTags.text = buildArtifactsAndTags
        }
    }

    private val buildArtifactsAndTags: CharSequence
        get() {
            val builder = SpannableStringBuilder()

            item.artifacts
                    .forEach {
                        val length = builder.length
                        builder.append("\u00a0${it.name}\u00a0")
                        builder.setSpan(ArtifactSpan(it), length, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }

            if (!builder.isEmpty()) {
                builder.append("\n")
            }

            item.tags
                    .forEach {
                        val length = builder.length
                        builder.append("\u00a0$it\u00a0")
                        builder.setSpan(TagSpan(it), length, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }

            return builder
        }

    class Holder(itemView: View) : Item.Holder(itemView) {
        val title: TextView = requireView(R.id.title)
        val description: TextView = requireView(R.id.description)
        val artifactsAndTags: TextView = requireView(R.id.artifacts_and_tags)
    }

    private class ArtifactSpan(val artifact: MarkwonArtifact) : ClickableSpan() {
        override fun onClick(widget: View) {
            Debug.i("clicked artifact: $artifact")
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
            ds.bgColor = Color.GREEN
        }
    }

    private class TagSpan(val tag: String) : ClickableSpan() {
        override fun onClick(widget: View) {
            Debug.i("clicked tag: $tag")
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
            ds.bgColor = Color.BLUE
        }
    }
}