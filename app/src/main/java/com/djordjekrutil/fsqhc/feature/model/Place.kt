package com.djordjekrutil.fsqhc.feature.model

data class Place(
    val fsqId: String,
    val name: String,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val categories: List<String>,
    val distance: Int?,
    val isFavorite: Boolean,
    val rating: Double? = null,
    val price: Int? = null,
    val isOpen: Boolean? = null,
    val photoUrl: String? = null,
    val details: PlaceDetails? = null
)