package com.djordjekrutil.fsqhc.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.net.URI

class UtilsTest {
    private fun extractCursorFromLinkHeaderJvm(linkHeader: String): String? {
        val urlRegex = Regex("<(.*?)>")
        val url = urlRegex.find(linkHeader)?.groups?.get(1)?.value ?: return null
        val uri = URI(url)
        val query = uri.query ?: return null
        return query.split('&').mapNotNull {
            val parts = it.split('=')
            if (parts.size == 2 && parts[0] == "cursor") parts[1] else null
        }.firstOrNull()
    }

    @Test
    fun `extractCursorFromLinkHeader should return cursor value if present`() {
        val header = "<https://api.foursquare.com/v3/places?cursor=abc123>; rel=next"
        val result = extractCursorFromLinkHeaderJvm(header)
        assertEquals("abc123", result)
    }

    @Test
    fun `extractCursorFromLinkHeader should return null if cursor not present`() {
        val header = "<https://api.foursquare.com/v3/places>; rel=next"
        val result = extractCursorFromLinkHeaderJvm(header)
        assertNull(result)
    }
} 