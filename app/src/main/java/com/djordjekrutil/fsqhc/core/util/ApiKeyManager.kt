package com.djordjekrutil.fsqhc.core.util

class ApiKeyManager {

    external fun getApiKey(): String
    external fun getApiUrl(): String

    companion object {
        init {
            System.loadLibrary("foursquarekeys")
        }

        @Volatile
        private var INSTANCE: ApiKeyManager? = null

        fun getInstance(): ApiKeyManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApiKeyManager().also { INSTANCE = it }
            }
        }
    }
}