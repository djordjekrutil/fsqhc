package com.djordjekrutil.fsqhc.feature.model

import com.google.gson.annotations.SerializedName

data class PlaceDto(
    @SerializedName("fsq_id") val fsq_id: String,
    val name: String,
    val location: LocationDto?,
    val categories: List<CategoryDto>?,
    val distance: Int?,
    val rating: Double?,
    val price: Int?,
    val hours: HoursDto?,
    val photos: List<PhotoDto>?
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

data class HoursDto(
    val display: String?,
    val is_local_holiday: Boolean?,
    val open_now: Boolean?,
    val regular: List<RegularHourDto>?
)

data class RegularHourDto(
    val close: String?,
    val day: Int,
    val open: String?
)

data class PhotoDto(
    val id: String,
    val prefix: String,
    val suffix: String,
    val width: Int,
    val height: Int
) {
    fun getPhotoUrl(size: String = "300x300"): String {
        return "${prefix}${size}${suffix}"
    }
}