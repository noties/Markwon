package io.noties.markwon.app.sample.ui.adapt

import android.content.Context
import android.text.Spanned
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.noties.adapt.Item
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.LinkResolver
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.app.BuildConfig
import io.noties.markwon.app.R
import io.noties.markwon.core.CoreProps
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.movement.MovementMethodPlugin
import org.commonmark.node.Link

class VersionItem : Item<VersionItem.Holder>(42L) {

    private lateinit var context: Context

    private val markwon: Markwon by lazy(LazyThreadSafetyMode.NONE) {
        Markwon.builder(context)
                .usePlugin(ImagesPlugin.create())
                .usePlugin(MovementMethodPlugin.link())
                .usePlugin(HtmlPlugin.create())
                .usePlugin(object : AbstractMarkwonPlugin() {
                    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                        builder.setFactory(Link::class.java) { configuration, props ->
                            LinkSpanNoUnderline(
                                    configuration.theme(),
                                    CoreProps.LINK_DESTINATION.require(props),
                                    configuration.linkResolver()
                            )
                        }
                    }
                })
                .build()
    }

    private val text: Spanned by lazy(LazyThreadSafetyMode.NONE) {
        val md = """
            <a href="${BuildConfig.GIT_REPOSITORY}/blob/master/CHANGELOG.md">
            
            
            ![stable](https://img.shields.io/maven-central/v/io.noties.markwon/core.svg?label=stable)
            ![snapshot](https://img.shields.io/nexus/s/https/oss.sonatype.org/io.noties.markwon/core.svg?label=snapshot)
            ![changelog](https://fonts.gstatic.com/s/i/materialicons/open_in_browser/v6/24px.svg?download=true)
            </a>
        """.trimIndent()
        markwon.toMarkdown(md)
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        context = parent.context
        return Holder(inflater.inflate(R.layout.adapt_version, parent, false))
    }

    override fun render(holder: Holder) {
        markwon.setParsedMarkdown(holder.textView, text)
    }

    class Holder(view: View) : Item.Holder(view) {
        val textView: TextView = requireView(R.id.text_view)
    }

    class LinkSpanNoUnderline(
            theme: MarkwonTheme,
            destination: String,
            resolver: LinkResolver
    ) : LinkSpan(theme, destination, resolver) {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }
}