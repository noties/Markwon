package io.noties.markwon.app.utils

import java.io.IOException
import java.io.InputStream
import java.util.Scanner

fun InputStream.readStringAndClose(): String {
    try {
        val scanner = Scanner(this).useDelimiter("\\A")
        if (scanner.hasNext()) {
            return scanner.next()
        }
        return ""
    } finally {
        try {
            close()
        } catch (e: IOException) {
            // ignored
        }
    }
}