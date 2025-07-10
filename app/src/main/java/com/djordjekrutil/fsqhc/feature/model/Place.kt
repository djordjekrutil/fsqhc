package com.djordjekrutil.fsqhc.feature.model

data class Place(
    val fsqId: String,
    val name: String,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val categories: List<String>,
    val distance: Int?
)