package com.djordjekrutil.fsqhc.feature.model

data class PlaceDetails(
    val description: String? = null,
    val website: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val hours: List<WorkingHours> = emptyList(),
    val photos: List<String> = emptyList(),
    val tips: List<Tip> = emptyList(),
    val rating: Double? = null,
    val ratingCount: Int? = null,
    val priceLevel: Int? = null,
    val features: List<String> = emptyList(),
    val menu: String? = null,
    val socialMedia: SocialMedia? = null,
    val popularTimes: List<PopularTime> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis()
)

data class WorkingHours(
    val dayOfWeek: Int,
    val openTime: String?,
    val closeTime: String?,
    val isOpen24Hours: Boolean = false,
    val isClosed: Boolean = false
)

data class Tip(
    val id: String? = null,
    val text: String,
    val authorName: String?,
    val createdAt: String,
    val likes: Int = 0
)

data class SocialMedia(
    val instagram: String? = null,
    val facebook: String? = null,
    val twitter: String? = null
)

data class PopularTime(
    val dayOfWeek: Int,
    val hour: Int,
    val popularity: Int
)