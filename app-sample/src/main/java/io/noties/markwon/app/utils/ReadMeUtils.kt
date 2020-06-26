package io.noties.markwon.app.utils

import android.net.Uri
import java.util.regex.Pattern

object ReadMeUtils {
    // username, repo, branch, lastPathSegment
    private val RE = Pattern.compile("^https:\\/\\/github\\.com\\/(\\w+?)\\/(\\w+?)\\/(?:blob|raw)\\/(\\w+?)\\/(.+)")

    data class GithubInfo(
            val username: String,
            val repository: String,
            val branch: String,
            val fileName: String
    )

    fun parseInfo(data: Uri?): GithubInfo? {

        if (data == null) {
            return null
        }

        val matcher = RE.matcher(data.toString())
        if (!matcher.matches()) {
            return null
        }

        return GithubInfo(
                username = matcher.group(1),
                repository = matcher.group(2),
                branch = matcher.group(3),
                fileName = matcher.group(4)
        )
    }

    fun buildRawGithubUrl(data: Uri): String {
        val info = parseInfo(data)
        return if (info == null) {
            data.toString()
        } else {
            "https://github.com/${info.username}/${info.repository}/raw/${info.branch}/${info.fileName}"
        }
    }
}