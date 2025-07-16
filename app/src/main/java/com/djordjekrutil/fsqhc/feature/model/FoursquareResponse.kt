package com.djordjekrutil.fsqhc.feature.model

data class FoursquareResponse(
    val results: List<PlaceDto>,
    val next: String? = null
)