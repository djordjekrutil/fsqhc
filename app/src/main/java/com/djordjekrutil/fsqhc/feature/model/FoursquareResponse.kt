package com.djordjekrutil.fsqhc.feature.model

data class FoursquareResponse(
    val results: List<PlaceDto>
)

data class PlaceDto(
    val fsq_id: String,
    val name: String,
    val location: LocationDto?,
    val categories: List<CategoryDto>?,
    val distance: Int?
)

data class LocationDto(
    val address: String?,
    val lat: Double?,
    val lng: Double?
)

data class CategoryDto(
    val name: String,
    val icon: IconDto?
)

data class IconDto(
    val prefix: String,
    val suffix: String
)