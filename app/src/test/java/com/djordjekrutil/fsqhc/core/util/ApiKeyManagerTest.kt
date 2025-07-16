package com.djordjekrutil.fsqhc.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ApiKeyManagerTest {
    @Test
    fun `getApiUrl should return correct url`() {
        val expected = "https://api.foursquare.com/"
        val actual = ApiKeyManager.getApiUrl()
        assertEquals(expected, actual)
    }
} 