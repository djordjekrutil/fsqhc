package com.djordjekrutil.fsqhc.feature.model

import com.google.gson.annotations.SerializedName

data class PlaceDetailsDto(
    @SerializedName("fsq_id") val fsq_id: String,
    val name: String,
    val location: LocationDto?,
    val categories: List<CategoryDto>?,
    val description: String?,
    val website: String?,
    val tel: String?,
    val email: String?,
    val hours: HoursDto?,
    val photos: List<PhotoDto>?,
    val tips: List<TipDto>?,
    val rating: Double?,
    @SerializedName("stats") val stats: StatsDto?,
    val price: Int?,
    val menu: String?,
    @SerializedName("social_media") val social_media: SocialMediaDto?,
    @SerializedName("popular_times") val popular_times: List<PopularTimeDto>?
)

data class TipDto(
    val id: String?,
    val text: String,
    val user: UserDto?,
    val created_at: String,
    val agree_count: Int?
)

data class UserDto(
    val first_name: String?,
    val last_name: String?
)

data class StatsDto(
    val total_photos: Int?,
    val total_ratings: Int?,
    val total_tips: Int?
)

data class SocialMediaDto(
    val instagram: String?,
    val facebook: String?,
    val twitter: String?
)

data class PopularTimeDto(
    val close: String?,
    val day: Int,
    val open: String?,
    val popularity: List<PopularityDto>?
)

data class PopularityDto(
    val hour: Int,
    val popularity: Int
)