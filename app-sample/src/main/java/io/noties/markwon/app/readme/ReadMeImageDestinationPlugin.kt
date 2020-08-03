package io.noties.markwon.app.readme

import android.net.Uri
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.app.utils.ReadMeUtils

class ReadMeImageDestinationPlugin(private val data: Uri?) : AbstractMarkwonPlugin() {
    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        val info = ReadMeUtils.parseInfo(data)
        if (info == null) {
            builder.imageDestinationProcessor(GithubImageDestinationProcessor())
        } else {
            builder.imageDestinationProcessor(GithubImageDestinationProcessor(
                    username = info.username,
                    repository = info.repository,
                    branch = info.branch
            ))
        }
    }
}