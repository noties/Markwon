package io.noties.markwon.app.readme

import android.view.View
import io.noties.markwon.LinkResolverDef
import io.noties.markwon.app.utils.ReadMeUtils

class ReadMeLinkResolver : LinkResolverDef() {

    override fun resolve(view: View, link: String) {
        val matcher = ReadMeUtils.RE_REPOSITORY.matcher(link)
        val url = if (matcher.matches()) {
            ReadMeUtils.buildRepositoryReadMeUrl(matcher.group(1), matcher.group(2))
        } else {
            link
        }
        super.resolve(view, url)
    }
}