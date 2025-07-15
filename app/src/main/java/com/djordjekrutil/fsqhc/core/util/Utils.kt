package com.djordjekrutil.fsqhc.core.util

import androidx.core.net.toUri

fun extractCursorFromLinkHeader(linkHeader: String): String? {
    val urlRegex = Regex("<(.*?)>")
    val url = urlRegex.find(linkHeader)?.groups?.get(1)?.value ?: return null

    val uri = url.toUri()
    return uri.getQueryParameter("cursor")
}