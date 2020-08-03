package io.noties.markwon.app.readme

import android.view.View
import io.noties.markwon.LinkResolverDef
import io.noties.markwon.app.utils.ReadMeUtils

class ReadMeLinkResolver : LinkResolverDef() {

    override fun resolve(view: View, link: String) {
        val info = ReadMeUtils.parseRepository(link)
        val url = if (info != null) {
            ReadMeUtils.buildRepositoryReadMeUrl(info.first, info.second)
        } else {
            link
        }
        super.resolve(view, url)
    }
}