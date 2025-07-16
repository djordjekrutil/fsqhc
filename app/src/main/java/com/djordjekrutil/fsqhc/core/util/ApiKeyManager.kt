package com.djordjekrutil.fsqhc.core.util

import com.djordjekrutil.fsqhc.BuildConfig

object ApiKeyManager {

    fun getApiKey(): String {
        return BuildConfig.FOURSQUARE_API_KEY
    }

    fun getApiUrl(): String {
        return "https://api.foursquare.com/"
    }
}