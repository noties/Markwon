package io.noties.markwon.app.utils

import android.net.Uri
import android.text.TextUtils
import java.util.regex.Pattern

object ReadMeUtils {

    // username, repo, branch, lastPathSegment
    @Suppress("RegExpRedundantEscape")
    private val RE_FILE = Pattern.compile("^https:\\/\\/github\\.com\\/([\\w-.]+?)\\/([\\w-.]+?)\\/(?:blob|raw)\\/([\\w-.]+?)\\/(.+)\$")

    @Suppress("RegExpRedundantEscape")
    private val RE_REPOSITORY: Pattern = Pattern.compile("^https:\\/\\/github.com\\/([\\w-.]+?)\\/([\\w-.]+?)\\/*\$")

    data class GithubInfo(
            val username: String,
            val repository: String,
            val branch: String,
            val fileName: String
    )

    fun parseRepository(url: String): Pair<String, String>? {
        val matcher = RE_REPOSITORY.matcher(url)
        val (user, repository) = if (matcher.matches()) {
            Pair(matcher.group(1), matcher.group(2))
        } else {
            Pair(null, null)
        }
        return if (TextUtils.isEmpty(user) || TextUtils.isEmpty(repository)) {
            null
        } else {
            Pair(user!!, repository!!)
        }
    }

    fun parseInfo(data: Uri?): GithubInfo? {

        if (data == null) {
            return null
        }

        val matcher = RE_FILE.matcher(data.toString())
        if (!matcher.matches()) {
            return null
        }

        return GithubInfo(
                username = matcher.group(1)!!,
                repository = matcher.group(2)!!,
                branch = matcher.group(3)!!,
                fileName = matcher.group(4)!!
        )
    }

    fun buildRawGithubUrl(data: Uri): String {
        val info = parseInfo(data)
        return if (info == null) {
            data.toString()
        } else {
            buildRawGithubUrl(info)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun buildRawGithubUrl(info: GithubInfo): String {
        return "https://github.com/${info.username}/${info.repository}/raw/${info.branch}/${info.fileName}"
    }

    fun buildRepositoryReadMeUrl(username: String, repository: String): String {
        return buildRawGithubUrl(GithubInfo(username, repository, "master", "README.md"))
    }
}